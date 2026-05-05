package com.example.expense_manager.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "budgets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount; // Hạn mức ngân sách

    @Column(name = "month_year", length = 7, nullable = false)
    private String monthYear; // Lưu định dạng tháng/năm, VD: "04/2026"

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 👇 ĐÂY LÀ DÒNG TAO VỪA THÊM VÀO ĐỂ LÀM TÍNH NĂNG "LẶP LẠI THÁNG SAU" 👇
    @Column(name = "is_recurring", nullable = false, columnDefinition = "boolean default false")
    private boolean isRecurring;
}