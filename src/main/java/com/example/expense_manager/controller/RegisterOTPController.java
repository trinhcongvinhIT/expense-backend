package com.example.expense_manager.controller;

import com.example.expense_manager.entity.User;
import com.example.expense_manager.repository.UserRepository;
import com.example.expense_manager.service.CategoryService; // 👈 1. THÊM IMPORT NÀY
import com.example.expense_manager.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class RegisterOTPController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryService categoryService; // 👈 2. GỌI THẰNG BƠM DANH MỤC VÀO ĐÂY

    // Tuyệt chiêu: Dùng bộ nhớ tạm (RAM) để lưu OTP đăng ký, không cần đụng Database
    private static final Map<String, String> otpStorage = new ConcurrentHashMap<>();

    // ==========================================
    // 1. API Gửi mã OTP để Đăng ký
    // ==========================================
    @PostMapping("/send-register-otp")
    public ResponseEntity<?> sendRegisterOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        // Kiểm tra xem email đã bị thằng khác đăng ký chưa
        if (userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.badRequest().body("Email này đã được đăng ký cho tài khoản khác!");
        }

        // Tạo mã OTP 6 số
        String otp = emailService.generateOtp();

        // Cất mã OTP vào bộ nhớ tạm
        otpStorage.put(email, otp);

        // Bắn email đi
        emailService.sendOtpEmail(email, otp);

        return ResponseEntity.ok("Mã xác nhận đã được gửi đến email của bạn!");
    }

    // ==========================================
    // 2. API Xác nhận OTP và Tạo tài khoản
    // ==========================================
    @PostMapping("/register-with-otp")
    public ResponseEntity<?> registerWithOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");
        String username = request.get("username");
        String password = request.get("password");

        // Móc mã OTP trong bộ nhớ tạm ra để đối chiếu
        String savedOtp = otpStorage.get(email);
        if (savedOtp == null || !savedOtp.equals(otp)) {
            return ResponseEntity.badRequest().body("Mã OTP không hợp lệ hoặc đã hết hạn!");
        }

        // Kiểm tra xem tên đăng nhập có bị trùng không
        if (userRepository.findByUsername(username).isPresent()) {
            return ResponseEntity.badRequest().body("Tên tài khoản này đã có người sử dụng, vui lòng chọn tên khác!");
        }

        try {
            // Vượt qua hết thì tiến hành TẠO TÀI KHOẢN MỚI
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(password);
            newUser.setEmail(email);

            // Lưu user và gán vào biến savedUser để lấy ID mới tạo
            User savedUser = userRepository.save(newUser);

            // 👇👇👇 3. BƠM DANH MỤC NGAY TẠI ĐÂY NÈ!!!
            categoryService.createDefaultCategoriesForUser(savedUser);

            // Tạo xong thì xóa cái OTP đi cho sạch bộ nhớ
            otpStorage.remove(email);

            // Tao đổi lại trả về thẳng đối tượng User để Frontend (React) lấy ID dùng luôn
            return ResponseEntity.ok(savedUser);

        } catch (Exception e) {
            e.printStackTrace(); // Kính chiếu yêu: Báo lỗi đỏ nếu MySQL dỗi
            return ResponseEntity.internalServerError().body("Lỗi hệ thống khi đăng ký: " + e.getMessage());
        }
    }
}