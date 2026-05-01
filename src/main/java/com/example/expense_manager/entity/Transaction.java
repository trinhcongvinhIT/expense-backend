package com.example.expense_manager.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount; // Số tiền

    @Column(length = 255)
    private String description; // Ghi chú

    @Column(name = "transaction_date", nullable = false)
    private LocalDate transactionDate; // Ngày giao dịch

    // Khóa ngoại nối sang bảng Category
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    // Khóa ngoại nối sang bảng User
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Thêm hàm này để tự động bốc loại Thu/Chi từ Danh Mục gửi lên cho React
    @jakarta.persistence.Transient // Đánh dấu để MySQL không đòi tìm cột này trong Database
    public String getType() {
        if (this.category != null && this.category.getType() != null) {
            return this.category.getType().name();
        }
        return "EXPENSE"; // Mặc định nếu lỗi thì gán là Chi
    }
}