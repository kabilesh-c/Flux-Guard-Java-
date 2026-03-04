package com.fraud.detection.config;

import com.fraud.detection.entity.Rule;
import com.fraud.detection.entity.User;
import com.fraud.detection.repository.RuleRepository;
import com.fraud.detection.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Initializes H2 database with sample data on startup
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RuleRepository ruleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        log.info("Initializing H2 database with sample data...");
        
        // Create admin user
        if (userRepository.findByEmail("admin@fraud-detection.com").isEmpty()) {
            User admin = User.builder()
                    .email("admin@fraud-detection.com")
                    .passwordHash(passwordEncoder.encode("Admin@123"))
                    .firstName("Admin")
                    .lastName("User")
                    .role(User.UserRole.ADMIN)
                    .build();
            userRepository.save(admin);
            log.info("Created admin user: admin@fraud-detection.com / Admin@123");
        }

        // Create sample rules
        if (ruleRepository.count() == 0) {
            createRule("RULE001", "High Amount Transaction", 
                    "Flag transactions over $10,000", 
                    "amount > 10000", 30, "HIGH", "FLAG");
            
            createRule("RULE002", "Suspicious Location", 
                    "Flag transactions from high-risk countries", 
                    "location != null && (location.country == 'NG' || location.country == 'PK')", 
                    25, "MEDIUM", "FLAG");
            
            createRule("RULE003", "Velocity Check", 
                    "Reject very large transactions", 
                    "amount > 50000", 50, "CRITICAL", "REJECT");
            
            createRule("RULE004", "Unusual Transaction Type", 
                    "Flag non-standard transaction types", 
                    "transactionType != 'TRANSFER' && transactionType != 'PAYMENT'", 
                    15, "LOW", "FLAG");
            
            createRule("RULE005", "Missing Device Info", 
                    "Flag transactions without device fingerprint", 
                    "deviceFingerprint == null || deviceFingerprint.isEmpty()", 
                    20, "MEDIUM", "FLAG");

            log.info("Created {} fraud detection rules", ruleRepository.count());
        }
        
        log.info("Database initialization complete!");
    }

    private void createRule(String ruleId, String name, String description, 
                          String expression, int weight, String severity, String action) {
        Rule rule = Rule.builder()
                .ruleId(ruleId)
                .name(name)
                .description(description)
                .expression(expression)
                .weight(weight)
                .severity(Rule.RuleSeverity.valueOf(severity))
                .action(Rule.RuleAction.valueOf(action))
                .active(true)
                .build();
        ruleRepository.save(rule);
    }
}
