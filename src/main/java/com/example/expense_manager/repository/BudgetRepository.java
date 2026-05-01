package com.example.expense_manager.repository;

import com.example.expense_manager.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    // Tìm hạn mức ngân sách của một danh mục cụ thể trong một tháng cụ thể
    Optional<Budget> findByUserIdAndCategoryIdAndMonthYear(Long userId, Long categoryId, String monthYear);
}