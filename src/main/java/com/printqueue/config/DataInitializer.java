package com.printqueue.config;

import com.printqueue.entity.Role;
import com.printqueue.entity.User;
import com.printqueue.entity.UserStatus;
import com.printqueue.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Database initializer — seeds required initial data on startup.
 *
 * Seeds:
 *   1. DEMO ADMIN USER:
 *      Email   : admin@digitalprint.com
 *      Password: Admin@123
 *
 *      ⚠️  IMPORTANT — DEMO CREDENTIAL NOTICE:
 *          This is a DEMONSTRATION credential for academic use only.
 *          DO NOT use this credential in any production deployment.
 *          Change the password immediately after first login in any
 *          real environment.
 *
 * The seeder is idempotent — it checks before inserting.
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final UserRepository userRepository;
    private final com.printqueue.repository.PrinterRepository printerRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner seedDatabase() {
        return args -> {
            seedAdminUser();
            seedPrinter();
            log.info("=== Database initialization complete ===");
        };
    }

    private void seedPrinter() {
        if (printerRepository.findByName("PRINTER-01").isEmpty()) {
            com.printqueue.entity.VirtualPrinter printer = com.printqueue.entity.VirtualPrinter.builder()
                    .name("PRINTER-01")
                    .model("Virtual Print Engine")
                    .location("Server Room")
                    .status(com.printqueue.entity.VirtualPrinterStatus.IDLE)
                    .totalJobsProcessed(0)
                    .build();
            printerRepository.save(printer);
            log.info("Seeded PRINTER-01 into database.");
        }
    }

    private void seedAdminUser() {
        String adminEmail = "admin@digitalprint.com";

        if (userRepository.existsByEmail(adminEmail)) {
            log.info("Admin user already exists — skipping seed.");
            return;
        }

        // ⚠️ DEMO CREDENTIAL — for academic demonstration only
        User admin = User.builder()
                .name("System Administrator")
                .email(adminEmail)
                .mobile("0000000000")
                .password(passwordEncoder.encode("Admin@123"))  // DEMO PASSWORD
                .role(Role.ADMIN)
                .status(UserStatus.ACTIVE)
                .build();

        userRepository.save(admin);

        log.warn("========================================================");
        log.warn("  DEMO ADMIN CREDENTIAL SEEDED — ACADEMIC USE ONLY");
        log.warn("  Email   : {}", adminEmail);
        log.warn("  Password: Admin@123  (CHANGE IN PRODUCTION)");
        log.warn("========================================================");
    }
}
