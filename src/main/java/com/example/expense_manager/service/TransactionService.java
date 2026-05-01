package com.example.expense_manager.service;

import com.example.expense_manager.dto.CategoryReportDto;
import com.example.expense_manager.dto.TransactionRequest;
import com.example.expense_manager.entity.Category;
import com.example.expense_manager.entity.CategoryType;
import com.example.expense_manager.entity.Transaction;
import com.example.expense_manager.entity.User;
import com.example.expense_manager.repository.CategoryRepository;
import com.example.expense_manager.repository.TransactionRepository;
import com.example.expense_manager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    // 1. Lấy giao dịch trong khoảng thời gian
    public List<Transaction> getTransactionsInTimeframe(Long userId, LocalDate startDate, LocalDate endDate) {
        return transactionRepository.findByUserIdAndTransactionDateBetween(userId, startDate, endDate);
    }

    // 2. Thêm mới giao dịch
    public Transaction createTransaction(TransactionRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy User"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Danh mục"));

        Transaction transaction = new Transaction();
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setTransactionDate(request.getTransactionDate());
        transaction.setUser(user);
        transaction.setCategory(category);

        return transactionRepository.save(transaction);
    }

    // 3. Tính toán Báo cáo Chi tiêu (Biểu đồ tròn)
    public List<CategoryReportDto> getExpenseReportByMonth(Long userId, int month, int year) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        // Lấy tất cả giao dịch trong tháng
        List<Transaction> transactions = transactionRepository.findByUserIdAndTransactionDateBetween(userId, startDate, endDate);

        BigDecimal totalExpense = BigDecimal.ZERO;
        Map<Category, BigDecimal> categoryTotals = new HashMap<>();

        // Cộng dồn tiền theo từng danh mục CHI TIÊU
        for (Transaction t : transactions) {
            if (t.getCategory().getType() == CategoryType.EXPENSE) {
                totalExpense = totalExpense.add(t.getAmount());
                categoryTotals.put(t.getCategory(),
                        categoryTotals.getOrDefault(t.getCategory(), BigDecimal.ZERO).add(t.getAmount()));
            }
        }

        List<CategoryReportDto> report = new ArrayList<>();
        if (totalExpense.compareTo(BigDecimal.ZERO) == 0) {
            return report; // Nếu không tiêu gì thì trả về list rỗng
        }

        // Tính phần trăm (%)
        for (Map.Entry<Category, BigDecimal> entry : categoryTotals.entrySet()) {
            Category cat = entry.getKey();
            BigDecimal amount = entry.getValue();

            // Công thức: (Số tiền danh mục / Tổng tiền) * 100
            double percentage = (amount.doubleValue() / totalExpense.doubleValue()) * 100;
            percentage = Math.round(percentage * 10.0) / 10.0; // Làm tròn 1 chữ số thập phân (VD: 16.7%)

            report.add(new CategoryReportDto(
                    cat.getId(), cat.getName(), cat.getColorCode(), cat.getIconName(), amount, percentage
            ));
        }

        // Sắp xếp chi tiêu từ nhiều nhất đến ít nhất cho đẹp
        report.sort((a, b) -> b.getTotalAmount().compareTo(a.getTotalAmount()));

        return report;
    }

    // =========================================================================
    // 4. THÊM MỚI: API SỬA GIAO DỊCH
    // =========================================================================
    public Transaction updateTransaction(Long id, TransactionRequest request) {
        // Tìm giao dịch cũ
        Transaction existingTransaction = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giao dịch với ID: " + id));

        // Cập nhật các trường dữ liệu (amount, description, date)
        existingTransaction.setAmount(request.getAmount());
        existingTransaction.setDescription(request.getDescription());
        existingTransaction.setTransactionDate(request.getTransactionDate());

        // Cập nhật lại Danh mục nếu có thay đổi
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy Danh mục"));
            existingTransaction.setCategory(category);
        }

        // Lưu đè lại vào database
        return transactionRepository.save(existingTransaction);
    }

    // =========================================================================
    // 5. THÊM MỚI: API XÓA GIAO DỊCH
    // =========================================================================
    public void deleteTransaction(Long id) {
        // Kiểm tra xem có tồn tại không trước khi xóa
        if (!transactionRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy giao dịch với ID: " + id);
        }
        transactionRepository.deleteById(id);
    }
}