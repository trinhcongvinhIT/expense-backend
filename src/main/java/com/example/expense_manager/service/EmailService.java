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

            String requestJson = "{"
                    + "\"sender\":{\"name\":\"Website Chi Tieu\",\"email\":\"" + EMAIL_NGUOI_GUI + "\"},"
                    + "\"to\":[{\"email\":\"" + toEmail + "\"}],"
                    + "\"subject\":\"Ma xac thuc OTP\","
                    + "\"htmlContent\":\"<h3>Ma OTP cua ban la: <b style='color:red;'>" + otp + "</b></h3>\""
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