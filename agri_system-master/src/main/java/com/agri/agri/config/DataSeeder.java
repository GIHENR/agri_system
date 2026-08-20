package com.agri.agri.config;

import com.agri.agri.entity.User;
import com.agri.agri.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;

    public DataSeeder(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Checks if the admin email already exists
        if (userRepository.findFirstByEmail("admin@agri.com").isEmpty()) {
            User admin = new User();
            admin.setUsername("System Admin");
            admin.setEmail("admin@agri.com");
            admin.setPassword("admin123");
            admin.setRole("ADMIN");
            admin.setVerified(true); // Admins bypass email verification

            userRepository.save(admin);
            System.out.println("✅ Master Admin account securely injected into database.");
        }
    }
}