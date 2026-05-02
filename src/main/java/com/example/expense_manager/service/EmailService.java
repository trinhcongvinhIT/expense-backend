package com.example.expense_manager.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EmailService {

    // 1. DÁN API KEY MÀY VỪA LẤY Ở BREVO VÀO ĐÂY (Nhớ giữ lại chữ xkeysib-)
    private final String BREVO_API_KEY = "xsmtpsib-5db8b54e9239543bc0afc1e090732d9179d333a1b18a5b20c7fd9563b757830c-gBhIXpDLuKyR7Fqk";

    // 2. ĐIỀN ĐÚNG EMAIL MÀY DÙNG ĐĂNG KÝ BREVO VÀO ĐÂY
    private final String EMAIL_NGUOI_GUI = "quanlychitieu24@gmail.com";

    public void sendOtpEmail(String toEmail, String otp) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = "https://api.brevo.com/v3/smtp/email";

            // Thiết lập Header để Brevo nhận diện mày
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", BREVO_API_KEY);

            // Tạo nội dung thư bằng JSON
            String requestJson = "{"
                    + "\"sender\":{\"name\":\"Hệ thống Quản Lý Chi Tiêu\",\"email\":\"" + EMAIL_NGUOI_GUI + "\"},"
                    + "\"to\":[{\"email\":\"" + toEmail + "\"}],"
                    + "\"subject\":\"Mã xác nhận đăng ký (OTP)\","
                    + "\"htmlContent\":\""
                    + "<html>"
                    + "<body>"
                    + "<h2>Chào bạn!</h2>"
                    + "<p>Mã xác nhận (OTP) của bạn là: <b style='color:red; font-size: 20px;'>" + otp + "</b></p>"
                    + "<p>Mã này có hiệu lực trong 5 phút. Vui lòng không cung cấp mã này cho bất kỳ ai.</p>"
                    + "<br/>"
                    + "<p>Trân trọng,<br/>Đội ngũ Website Quản Lý Chi Tiêu</p>"
                    + "</body>"
                    + "</html>\""
                    + "}";

            HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);

            // Gửi yêu cầu qua cổng 443 (HTTP) - Không bao giờ bị Render chặn
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("✅ Đã gửi OTP qua Brevo API thành công!");
            } else {
                System.out.println("❌ Brevo báo lỗi: " + response.getBody());
            }

        } catch (Exception e) {
            System.out.println("❌ Lỗi kết nối API: " + e.getMessage());
        }
    }
}