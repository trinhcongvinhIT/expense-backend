package com.example.expense_manager.controller;

import com.example.expense_manager.entity.Budget;
import com.example.expense_manager.entity.Category;
import com.example.expense_manager.entity.User;
import com.example.expense_manager.repository.BudgetRepository;
import com.example.expense_manager.repository.CategoryRepository;
import com.example.expense_manager.repository.UserRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/budgets")
@CrossOrigin(origins = "*")
public class BudgetController {

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping("/{userId}")
    public List<Budget> getBudgets(@PathVariable Long userId, @RequestParam String monthYear) {
        return budgetRepository.findByUser_IdAndMonthYear(userId, monthYear);
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveBudget(@RequestBody BudgetRequest request) {
        User user = userRepository.findById(request.getUserId()).orElse(null);
        Category category = categoryRepository.findById(request.getCategoryId()).orElse(null);

        if (user == null || category == null) {
            return ResponseEntity.badRequest().body("Lỗi: Không tìm thấy User hoặc Danh mục!");
        }

        // Gọi hàm từ Repository (Hết đỏ dòng này)
        Optional<Budget> existingBudget = budgetRepository.findByUser_IdAndCategory_IdAndMonthYear(
                request.getUserId(), request.getCategoryId(), request.getMonthYear());

        Budget budget;
        if (existingBudget.isPresent()) {
            budget = existingBudget.get();
        } else {
            budget = new Budget();
            budget.setUser(user);
            budget.setCategory(category);
            budget.setMonthYear(request.getMonthYear());
        }

        budget.setAmount(request.getAmount());
        budget.setRecurring(request.isRecurring()); // (Hết đỏ dòng này)

        budgetRepository.save(budget);
        return ResponseEntity.ok(budget);
    }
}

// Class này để hứng dữ liệu từ React gửi lên
@Data
class BudgetRequest {
    private Long userId;
    private Long categoryId;
    private BigDecimal amount;
    private String monthYear;
    private boolean recurring; // Khớp với Frontend gửi lên

    // Getter cho boolean hay bị lỗi nên tao viết tay cho chắc
    public boolean isRecurring() {
        return recurring;
    }
}