package com.smartfood.wastemanagement;

import com.smartfood.wastemanagement.entity.AppUser;
import com.smartfood.wastemanagement.enums.Role;
import com.smartfood.wastemanagement.enums.VerificationStatus;
import com.smartfood.wastemanagement.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.nio.file.Files;
import java.nio.file.Path;

@SpringBootApplication
public class SmartFoodApplication {
    public static void main(String[] args) { SpringApplication.run(SmartFoodApplication.class, args); }

    @Bean CommandLineRunner setup(AppUserRepository users, PasswordEncoder encoder, @Value("${app.upload-dir:uploads}") String uploadDir) {
        return args -> {
            Files.createDirectories(Path.of(uploadDir));
            String email = System.getenv().getOrDefault("ADMIN_EMAIL", "admin@smartfood.local");
            String password = System.getenv().getOrDefault("ADMIN_PASSWORD", "Admin@12345");
            if (users.findByEmailIgnoreCase(email).isEmpty()) {
                AppUser admin = new AppUser(); admin.setName("System Admin"); admin.setEmail(email);
                admin.setPassword(encoder.encode(password)); admin.setRole(Role.ADMIN);
                admin.setVerificationStatus(VerificationStatus.APPROVED); admin.setEnabled(true);
                users.save(admin);
                System.out.println("Seeded admin account: " + email + " (set ADMIN_PASSWORD to change default)");
            }
        };
    }
}