package com.example.expense_manager.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TransactionRequest {
    private BigDecimal amount; // Số tiền
    private String description; // Ghi chú
    private LocalDate transactionDate; // Ngày giao dịch
    private Long categoryId; // Thuộc danh mục nào
    private Long userId; // Của user nào
}