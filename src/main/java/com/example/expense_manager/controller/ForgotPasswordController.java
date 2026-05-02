package com.example.expense_manager.controller;

import com.example.expense_manager.entity.User;
import com.example.expense_manager.repository.UserRepository;
import com.example.expense_manager.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class ForgotPasswordController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    // ==========================================
    // BƯỚC 1: API Yêu cầu gửi mã OTP
    // ==========================================
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        return userRepository.findByEmail(email).map(user -> {
            String otp = emailService.generateOtp();
            user.setOtp(otp);
            userRepository.save(user);

            boolean isSent = emailService.sendOtpEmail(email, otp);
            if (!isSent) {
                return ResponseEntity.status(500).body("Lỗi hệ thống gửi mail! Kiểm tra API Key.");
            }

            return ResponseEntity.ok("Mã OTP đã được gửi thành công!");
        }).orElse(ResponseEntity.badRequest().body("Không tìm thấy email!"));
    }

    // ==========================================
    // BƯỚC 2: API Xác nhận mã OTP
    // ==========================================
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");
        return userRepository.findByEmail(email)
                .filter(u -> u.getOtp() != null && u.getOtp().equals(otp))
                .map(u -> ResponseEntity.ok("Xác nhận thành công!"))
                .orElse(ResponseEntity.badRequest().body("Mã sai hoặc hết hạn!"));
    }

    // ==========================================
    // BƯỚC 3: API Cập nhật Mật khẩu mới (Bản vá lỗi mất tích)
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

        // Xóa mã OTP đi để tránh bị kẻ gian dùng lại (Bảo mật)
        user.setOtp(null);
        userRepository.save(user);

        return ResponseEntity.ok("Đổi mật khẩu thành công! Bạn có thể đăng nhập ngay.");
    }
}