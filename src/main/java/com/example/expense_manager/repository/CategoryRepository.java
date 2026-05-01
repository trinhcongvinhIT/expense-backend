package com.example.expense_manager.repository;

import com.example.expense_manager.entity.Category;
import com.example.expense_manager.entity.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // Lấy danh sách danh mục (Thu/Chi) của riêng một User cụ thể
    List<Category> findByUserIdAndType(Long userId, CategoryType type);
}