package com.example.expense_manager.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Random;

@Service
public class EmailService {

    // ⚠️ Đọc API Key từ file cấu hình ảo (Tuyệt mật)
    @Value("${brevo.api.key}")
    private String brevoApiKey;

    // Email đăng ký Brevo của mày
    private final String EMAIL_NGUOI_GUI = "quanlychitieu24@gmail.com";

    public String generateOtp() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    public boolean sendOtpEmail(String toEmail, String otp) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = "https://api.brevo.com/v3/smtp/email";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Dùng cái biến bí mật đã được mã hóa ở trên
            headers.set("api-key", brevoApiKey);

            // 1. Dựng sẵn một cái khung giao diện HTML siêu đẹp, Responsive xịn xò
            String htmlBody = "<div style='font-family: \"Segoe UI\", Arial, sans-serif; background-color: #f8fafc; padding: 40px 20px;'>"
                    + "<div style='max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 16px; overflow: hidden; box-shadow: 0 10px 25px rgba(0,0,0,0.05);'>"
                    // Phần Header (Màu Xanh Ngọc - Teal)
                    + "<div style='background-color: #0d9488; padding: 30px; text-align: center;'>"
                    + "<h2 style='color: #ffffff; margin: 0; font-size: 26px; letter-spacing: 1px;'>💰 QUẢN LÝ CHI TIÊU</h2>"
                    + "</div>"
                    // Phần Nội dung
                    + "<div style='padding: 40px 30px; text-align: center; color: #334155;'>"
                    + "<h3 style='font-size: 22px; margin-top: 0; color: #1e293b;'>Xác Thực Tài Khoản</h3>"
                    + "<p style='font-size: 16px; line-height: 1.6; color: #64748b;'>Chào bạn,<br>Bạn đang thực hiện thao tác bảo mật trên hệ thống. Dưới đây là mã xác nhận (OTP) của bạn:</p>"
                    // Khung chứa mã OTP nổi bật
                    + "<div style='margin: 35px auto; background-color: #f0fdfa; border: 2px dashed #14b8a6; border-radius: 12px; padding: 20px; display: inline-block; min-width: 200px;'>"
                    + "<span style='font-size: 40px; font-weight: 900; color: #0f766e; letter-spacing: 10px;'>" + otp + "</span>"
                    + "</div>"
                    // Cảnh báo đỏ
                    + "<p style='font-size: 15px; color: #e11d48; font-weight: bold; margin-bottom: 5px;'>⚠️ TUYỆT ĐỐI KHÔNG CHIA SẺ MÃ NÀY</p>"
                    + "<p style='font-size: 14px; color: #94a3b8; margin-top: 0;'>Mã có hiệu lực trong 5 phút. Nếu bạn không yêu cầu mã này, vui lòng bỏ qua email.</p>"
                    + "</div>"
                    // Phần Footer (Chữ ký Đồ án)
                    + "<div style='background-color: #f1f5f9; padding: 20px; text-align: center; font-size: 13px; color: #94a3b8; border-top: 1px solid #e2e8f0;'>"
                    + "<p style='margin: 0; font-weight: bold;'>© 2026 Trịnh Công Vĩnh - Đồ Án Tốt Nghiệp</p>"
                    + "<p style='margin: 5px 0 0 0;'>Hệ thống Quản lý Chi tiêu Cá nhân v1.0</p>"
                    + "</div>"
                    + "</div>"
                    + "</div>";

            // 2. Nhét cái khung HTML đó vào Payload JSON của Brevo
            String requestJson = "{"
                    + "\"sender\":{\"name\":\"Hệ Thống Chi Tiêu\",\"email\":\"" + EMAIL_NGUOI_GUI + "\"},"
                    + "\"to\":[{\"email\":\"" + toEmail + "\"}],"
                    + "\"subject\":\"Mã Xác Nhận (OTP) - Quản Lý Chi Tiêu\","
                    + "\"htmlContent\":\"" + htmlBody + "\""
                    + "}";

            HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            System.out.println("✅ Goi mail API thanh cong!");
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            System.out.println("❌ Loi API: " + e.getMessage());
            return false;
        }
    }
}