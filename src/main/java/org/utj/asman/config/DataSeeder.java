package org.utj.asman.config;

import org.utj.asman.model.User;
import org.utj.asman.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Check if admin exists
        if (!userRepository.findByUsername("admin").isPresent()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setName("Admin");
            // This encrypts 'Enthralment@24!' so it can be stored securely
            admin.setPassword(passwordEncoder.encode("Enthralment@24!")); 
            admin.setRole("ROLE_ADMIN");
            admin.setEnabled(true);
            
            userRepository.save(admin);
            System.out.println("✅ Default Admin User Created: admin / password123");
        }
    }
}