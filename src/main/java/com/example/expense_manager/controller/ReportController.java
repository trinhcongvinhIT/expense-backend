package com.example.expense_manager.controller;

import com.example.expense_manager.dto.CategoryReportDto;
import com.example.expense_manager.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin; // Mới thêm: Import thư viện
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")// Mới thêm: Bùa thông chốt CORS
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final TransactionService transactionService;

    // API nhả dữ liệu để vẽ biểu đồ tròn
    @GetMapping("/donut-chart")
    public ResponseEntity<List<CategoryReportDto>> getDonutChartData(
            @RequestParam Long userId,
            @RequestParam int month,
            @RequestParam int year) {

        List<CategoryReportDto> report = transactionService.getExpenseReportByMonth(userId, month, year);
        return ResponseEntity.ok(report);
    }
}