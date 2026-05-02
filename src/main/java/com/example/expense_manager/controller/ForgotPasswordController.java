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

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        return userRepository.findByEmail(email).map(user -> {
            String otp = emailService.generateOtp();
            user.setOtp(otp);
            userRepository.save(user);
            emailService.sendOtpEmail(email, otp);
            return ResponseEntity.ok("Mã OTP đã được gửi!");
        }).orElse(ResponseEntity.badRequest().body("Không tìm thấy email!"));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");
        return userRepository.findByEmail(email)
                .filter(u -> u.getOtp() != null && u.getOtp().equals(otp))
                .map(u -> ResponseEntity.ok("Xác nhận thành công!"))
                .orElse(ResponseEntity.badRequest().body("Mã sai hoặc hết hạn!"));
    }
}