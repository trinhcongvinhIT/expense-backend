package com.example.expense_manager.controller;

import com.example.expense_manager.entity.Category;
import com.example.expense_manager.entity.CategoryType;
import com.example.expense_manager.entity.User;
import com.example.expense_manager.repository.UserRepository;
import com.example.expense_manager.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<Category>> getCategories(
            @RequestParam Long userId,
            @RequestParam CategoryType type) {

        List<Category> categories = categoryService.getCategories(userId, type);
        return ResponseEntity.ok(categories);
    }

    // 🔥 CODE BỌC THÉP: KIỂM TRA TẬN RĂNG CHỐNG LỖI NULL 🔥
    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody Map<String, Object> payload) {
        try {
            // 1. Kiểm tra Tên danh mục
            if (payload.get("name") == null) {
                return ResponseEntity.badRequest().body("Lỗi: Tên danh mục không được để trống!");
            }
            String name = payload.get("name").toString();

            // 2. Kiểm tra Loại
            if (payload.get("type") == null) {
                return ResponseEntity.badRequest().body("Lỗi: Loại danh mục không được để trống!");
            }
            String typeStr = payload.get("type").toString();

            // 3. Móc userId ra một cách an toàn nhất (chống NullPointerException)
            Long userId = null;
            if (payload.get("userId") != null) {
                userId = Long.valueOf(payload.get("userId").toString());
            } else if (payload.get("user") != null) {
                Map<String, Object> userMap = (Map<String, Object>) payload.get("user");
                if (userMap.get("id") != null) {
                    userId = Long.valueOf(userMap.get("id").toString());
                }
            }

            // Nếu móc mãi vẫn null -> Khách chưa đăng nhập!
            if (userId == null) {
                return ResponseEntity.badRequest().body("Lỗi: Không tìm thấy ID người dùng. Mày đã đăng nhập chưa?");
            }

            // 4. Tìm User trong DB
            User realUser = userRepository.findById(userId).orElse(null);
            if (realUser == null) {
                return ResponseEntity.badRequest().body("Lỗi: Tài khoản không tồn tại trong hệ thống!");
            }

            // 5. Lắp ráp và lưu
            Category category = new Category();
            category.setName(name);
            category.setType(CategoryType.valueOf(typeStr));
            category.setUser(realUser);

            Category newCategory = categoryService.saveCategory(category);
            return ResponseEntity.ok(newCategory);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Lỗi Server: " + e.getMessage());
        }
    }
}