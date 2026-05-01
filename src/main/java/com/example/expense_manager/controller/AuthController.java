package com.example.expense_manager.controller;

import com.example.expense_manager.entity.User;
import com.example.expense_manager.repository.UserRepository;
import com.example.expense_manager.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*") // Mở cửa cho tất cả các port (như 3000, 3001) gọi vào
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final CategoryService categoryService;

    // ==========================================
    // 1. API ĐĂNG KÝ TÀI KHOẢN MỚI
    // ==========================================
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        // Kiểm tra xem tên tài khoản đã bị ai đặt chưa
        if (userRepository.existsByUsername(user.getUsername())) {
            return ResponseEntity.badRequest().body("Tên tài khoản này đã có người sử dụng!");
        }

        try {
            // Bước 1: Lưu tài khoản mới vào Database
            User savedUser = userRepository.save(user);

            // Bước 2: Tự động "bơm" toàn bộ danh mục mặc định cho user vừa tạo
            categoryService.createDefaultCategoriesForUser(savedUser);

            return ResponseEntity.ok(savedUser);
        } catch (Exception e) {
            // 👇👇👇 ĐÃ THÊM DÒNG NÀY ĐỂ ÉP NÓ IN LỖI ĐỎ RA MÀN HÌNH INTELLIJ
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Lỗi hệ thống khi đăng ký: " + e.getMessage());
        }
    }

    // ==========================================
    // 2. API ĐĂNG NHẬP
    // ==========================================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        // Tìm user theo username trong Database
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // So sánh mật khẩu
            if (user.getPassword().equals(password)) {
                // Đăng nhập thành công
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.status(401).body("Mật khẩu không chính xác!");
            }
        } else {
            return ResponseEntity.status(404).body("Tài khoản không tồn tại!");
        }
    }
}