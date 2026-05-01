package com.example.expense_manager.controller;

import com.example.expense_manager.dto.TransactionRequest;
import com.example.expense_manager.entity.Transaction;
import com.example.expense_manager.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    // 1. API Thêm mới giao dịch
    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@RequestBody TransactionRequest request) {
        try {
            Transaction savedTransaction = transactionService.createTransaction(request);
            return ResponseEntity.ok(savedTransaction);
        } catch (Exception e) {
            e.printStackTrace(); // In lỗi ra để soi cho kỹ
            return ResponseEntity.internalServerError().build();
        }
    }

    // 2. API Lấy giao dịch (Đã thêm lọc theo tháng/năm)
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Transaction>> getTransactionsByUser(
            @PathVariable Long userId,
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Boolean all) { // 👈 Thêm cái nút công tắc 'all'

        // NẾU FRONTEND BẬT CÔNG TẮC "TẤT CẢ" -> LẤY TỪ NĂM 2000 ĐẾN 2100
        if (Boolean.TRUE.equals(all)) {
            LocalDate start = LocalDate.of(2000, 1, 1);
            LocalDate end = LocalDate.of(2100, 12, 31);
            List<Transaction> transactions = transactionService.getTransactionsInTimeframe(userId, start, end);
            return ResponseEntity.ok(transactions);
        }

        // NẾU KHÔNG BẬT CÔNG TẮC -> LẤY THEO THÁNG NHƯ BÌNH THƯỜNG
        int targetMonth = (month != null) ? month : LocalDate.now().getMonthValue();
        int targetYear = (year != null) ? year : LocalDate.now().getYear();

        LocalDate start = LocalDate.of(targetYear, targetMonth, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<Transaction> transactions = transactionService.getTransactionsInTimeframe(userId, start, end);
        return ResponseEntity.ok(transactions);
    }

    // 3. API SỬA GIAO DỊCH
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTransaction(@PathVariable Long id, @RequestBody TransactionRequest request) {
        try {
            Transaction updatedTransaction = transactionService.updateTransaction(id, request);
            return ResponseEntity.ok(updatedTransaction);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi sửa giao dịch: " + e.getMessage());
        }
    }

    // 4. API XÓA GIAO DỊCH
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTransaction(@PathVariable Long id) {
        try {
            transactionService.deleteTransaction(id);
            return ResponseEntity.ok().body("Xóa giao dịch thành công!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi xóa giao dịch: " + e.getMessage());
        }
    }
}