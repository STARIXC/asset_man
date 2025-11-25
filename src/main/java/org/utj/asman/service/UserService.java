package org.utj.asman.service;

import org.utj.asman.model.User;
import org.utj.asman.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Retrieves all users.
     */
    public List<User> findAll() {
        return userRepository.findAll();
    }

    /**
     * Retrieves a user by ID.
     */
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Saves a new user or updates an existing one.
     * Handles password encryption logic.
     */
    public User save(User user) {
        // 1. Check if we are creating a new user or updating an existing one
        if (user.getId() == null) {
            // New User: Always encode the password
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            // Updating User: Check if password field is empty or changed
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            } else {
                // If password is not provided during update, keep the old one
                // This requires fetching the existing user first
                User existingUser = userRepository.findById(user.getId())
                        .orElseThrow(() -> new RuntimeException("User not found"));
                user.setPassword(existingUser.getPassword());
            }
        }

        // Set default role if missing
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("ROLE_USER");
        }

        // Ensure enabled is set
        if (!user.isEnabled()) {
            user.setEnabled(true);
        }

        return userRepository.save(user);
    }

    /**
     * Deletes a user by ID.
     */
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
}