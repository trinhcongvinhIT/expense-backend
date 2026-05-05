package com.example.expense_manager.repository;

import com.example.expense_manager.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    // Tìm theo ID của User và Category (Spring Boot tự chui vào Object lấy ID)
    Optional<Budget> findByUser_IdAndCategory_IdAndMonthYear(Long userId, Long categoryId, String monthYear);

    List<Budget> findByUser_IdAndMonthYear(Long userId, String monthYear);
}