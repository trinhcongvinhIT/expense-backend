package com.example.expense_manager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        // Cấu hình máy chủ Google
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        // Nhồi trực tiếp Tài khoản & Mật khẩu ứng dụng vào đây (Không sợ lỗi file ẩn)
        mailSender.setUsername("quanlychitieu24@gmail.com");
        mailSender.setPassword(System.getenv("EMAIL_PASS"));

        // Cấu hình các thông số bảo mật
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true"); // Bật soi lỗi
        props.put("mail.smtp.ssl.trust", "*");
        return mailSender;
    }
}