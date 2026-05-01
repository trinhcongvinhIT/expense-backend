package com.example.expense_manager.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.util.Random;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // Thuật toán đẻ ra 6 số ngẫu nhiên
    public String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    // Hàm tự động gửi email
    public void sendOtpEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Mã xác nhận OTP - Website Quản Lý Chi Tiêu");
        message.setText("Chào bạn,\n\nMã xác nhận (OTP) để lấy lại mật khẩu của bạn là: " + otp +
                "\n\nVui lòng không chia sẻ mã này cho bất kỳ ai để bảo mật tài khoản.\n\nTrân trọng!");

        mailSender.send(message);
    }
}
