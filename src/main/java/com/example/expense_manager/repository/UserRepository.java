package com.example.expense_manager.repository;

import com.example.expense_manager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Tự động sinh câu lệnh SQL tìm user theo username (Dùng cho lúc Đăng nhập)
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    // Tìm kiếm user theo email
    Optional<User> findByEmail(String email);
}