package com.medichain.iam.config;

import com.medichain.iam.entity.Role;
import com.medichain.iam.entity.User;
import com.medichain.iam.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@Configuration
@RequiredArgsConstructor
@Slf4j
public class AdminInitializer {

    @Bean
    CommandLineRunner initAdmin(
            UserRepository userRepository,
            BCryptPasswordEncoder passwordEncoder) {

        return args -> {
            String adminEmail = "admin@medichain.com";
            if (userRepository.findByEmail(adminEmail).isEmpty()) {
                User admin = User.builder()
                        .name("System Admin")
                        .email(adminEmail)
                        .phone("9876543210")
                        .password(passwordEncoder.encode("Admin@123"))
                        .role(Role.ADMIN)
                        .active(true)
                        .build();
                userRepository.save(admin);
                log.info("Default admin user seeded successfully");
            } else {
                log.info("Admin user already exists — skipping seed");
            }
        };
    }
}