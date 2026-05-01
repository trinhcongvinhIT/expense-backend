package com.example.expense_manager.controller;

// 3 dòng Import siêu quan trọng để nó không bị đỏ:
import com.example.expense_manager.entity.User;
import com.example.expense_manager.repository.UserRepository;
import com.example.expense_manager.service.EmailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*") // Mở cổng cho Frontend ReactJS gọi vào không bị chặn (CORS)
public class ForgotPasswordController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    // ==========================================
    // 1. API Yêu cầu gửi mã OTP
    // ==========================================
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest().body("Không tìm thấy tài khoản nào đăng ký với email này!");
        }

        // Đẻ mã OTP 6 số và lưu vào CSDL
        String otp = emailService.generateOtp();
        user.setOtp(otp);
        userRepository.save(user);

        // Bắn email cho người dùng
        emailService.sendOtpEmail(email, otp);
        return ResponseEntity.ok("Mã OTP đã được gửi thành công đến email của bạn!");
    }

    // ==========================================
    // 2. API Xác nhận mã OTP
    // ==========================================
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");

        User user = userRepository.findByEmail(email).orElse(null);

        // Kiểm tra xem mã nhập vào có khớp với mã lưu trong CSDL không
        if (user == null || user.getOtp() == null || !user.getOtp().equals(otp)) {
            return ResponseEntity.badRequest().body("Mã OTP không hợp lệ hoặc đã hết hạn!");
        }

        return ResponseEntity.ok("Xác nhận OTP thành công!");
    }

    // ==========================================
    // 3. API Cập nhật Mật khẩu mới
    // ==========================================
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String newPassword = request.get("newPassword");

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body("Lỗi xác thực người dùng!");
        }

        // Cập nhật mật khẩu mới
        user.setPassword(newPassword);

        // Xóa mã OTP đi để tránh bị kẻ gian dùng lại
        user.setOtp(null);
        userRepository.save(user);

        return ResponseEntity.ok("Đổi mật khẩu thành công! Bạn có thể đăng nhập ngay.");
    }
}