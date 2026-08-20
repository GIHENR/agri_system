package com.agri.agri.repository;

import com.agri.agri.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findFirstByEmail(String email);
    Optional<User> findByVerificationCode(String verificationCode);
    // Fetch users by their role and verification status
    List<User> findByRoleAndIsVerified(String role, boolean isVerified);
    
}