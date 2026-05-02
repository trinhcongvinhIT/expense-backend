package com.example.expense_manager.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Random;

@Service
public class EmailService {

    // ⚠️ DÁN API KEY CỦA BREVO VÀO ĐÂY
    private final String BREVO_API_KEY = "xkeysib-cai-chuoi-api-key-cua-may";
    private final String EMAIL_NGUOI_GUI = "quanlychitieu24@gmail.com";

    public String generateOtp() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    public void sendOtpEmail(String toEmail, String otp) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = "https://api.brevo.com/v3/smtp/email";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", BREVO_API_KEY);

            String requestJson = "{"
                    + "\"sender\":{\"name\":\"Website Chi Tieu\",\"email\":\"" + EMAIL_NGUOI_GUI + "\"},"
                    + "\"to\":[{\"email\":\"" + toEmail + "\"}],"
                    + "\"subject\":\"Ma xac thuc OTP\","
                    + "\"htmlContent\":\"<h3>Ma OTP cua ban la: <b style='color:red;'>" + otp + "</b></h3>\""
                    + "}";

            HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
            restTemplate.postForEntity(url, entity, String.class);
            System.out.println("✅ Goi mail API thanh cong!");
        } catch (Exception e) {
            System.out.println("❌ Loi API: " + e.getMessage());
        }
    }
}