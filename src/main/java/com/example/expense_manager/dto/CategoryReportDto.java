package com.example.expense_manager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryReportDto {
    private Long categoryId;
    private String categoryName;
    private String colorCode; // Cần mã màu để nhét vào biểu đồ
    private String iconName;
    private BigDecimal totalAmount; // Tổng tiền chi cho danh mục này
    private double percentage; // Phần trăm (%)
}