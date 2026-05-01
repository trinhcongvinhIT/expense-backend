package com.example.expense_manager.controller;

import com.example.expense_manager.entity.Category;
import com.example.expense_manager.entity.CategoryType;
import com.example.expense_manager.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin; // Mới thêm: Import thư viện CORS
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*") // Mới thêm: Cho phép React gọi sang
@RestController // Đánh dấu đây là Controller trả về JSON
@RequestMapping("/api/categories") // Đường dẫn gốc của API này
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // API lấy danh sách theo loại (Thu/Chi) và userId
    @GetMapping
    public ResponseEntity<List<Category>> getCategories(
            @RequestParam Long userId,
            @RequestParam CategoryType type) {

        List<Category> categories = categoryService.getCategories(userId, type);
        return ResponseEntity.ok(categories);
    }
}