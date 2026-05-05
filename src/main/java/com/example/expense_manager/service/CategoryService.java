package com.example.expense_manager.service;

import com.example.expense_manager.entity.Category;
import com.example.expense_manager.entity.CategoryType;
import com.example.expense_manager.entity.User;
import com.example.expense_manager.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    // Hàm lấy danh sách danh mục (Thu hoặc Chi)
    public List<Category> getCategories(Long userId, CategoryType type) {
        return categoryRepository.findByUserIdAndType(userId, type);
    }

    // Hàm hỗ trợ tạo nhanh 1 danh mục
    private Category createCategory(String name, CategoryType type, String icon, String color, User user) {
        Category category = new Category();
        category.setName(name);
        category.setType(type);
        category.setIconName(icon);
        category.setColorCode(color);
        category.setDescription("Danh mục " + name);
        category.setUser(user);
        return category;
    }

    // Hàm tự động bơm toàn bộ danh mục mặc định cho user mới
    public void createDefaultCategoriesForUser(User user) {
        List<Category> defaultCategories = new ArrayList<>();

        // KHOẢN CHI (EXPENSE)
        defaultCategories.add(createCategory("Ăn uống", CategoryType.EXPENSE, "utensils", "#ff8c00", user));
        defaultCategories.add(createCategory("Chi tiêu hàng ngày", CategoryType.EXPENSE, "shopping-cart", "#32CD32", user));
        defaultCategories.add(createCategory("Quần áo", CategoryType.EXPENSE, "tshirt", "#4169E1", user));
        defaultCategories.add(createCategory("Mỹ phẩm", CategoryType.EXPENSE, "magic", "#FF69B4", user));
        defaultCategories.add(createCategory("Phí giao lưu", CategoryType.EXPENSE, "glass-cheers", "#FFD700", user));
        defaultCategories.add(createCategory("Y tế", CategoryType.EXPENSE, "pills", "#20B2AA", user));
        defaultCategories.add(createCategory("Giáo dục", CategoryType.EXPENSE, "book", "#DC143C", user));
        defaultCategories.add(createCategory("Tiền điện", CategoryType.EXPENSE, "bolt", "#00BFFF", user));
        defaultCategories.add(createCategory("Đi lại", CategoryType.EXPENSE, "train", "#8B4513", user));
        defaultCategories.add(createCategory("Tiền nhà", CategoryType.EXPENSE, "home", "#DAA520", user));

        // KHOẢN THU (INCOME)
        defaultCategories.add(createCategory("Tiền lương", CategoryType.INCOME, "wallet", "#32CD32", user));
        defaultCategories.add(createCategory("Tiền phụ cấp", CategoryType.INCOME, "piggy-bank", "#FFA07A", user));
        defaultCategories.add(createCategory("Tiền thưởng", CategoryType.INCOME, "gift", "#FF6347", user));
        defaultCategories.add(createCategory("Thu nhập phụ", CategoryType.INCOME, "money-bag", "#1E90FF", user));
        defaultCategories.add(createCategory("Đầu tư", CategoryType.INCOME, "coins", "#20B2AA", user));

        // Lưu toàn bộ vào Database cùng một lúc
        categoryRepository.saveAll(defaultCategories);
    }
    // Hàm lưu danh mục mới vào Database
    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }
}