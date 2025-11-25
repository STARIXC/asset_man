package org.utj.asman.repository;

import org.utj.asman.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repository interface for managing User entities.
 * Extends JpaRepository to inherit standard CRUD operations.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a User by their unique username (used for login/authentication).
     * @param username The username of the user.
     * @return An Optional containing the found User or empty if not found.
     */
    Optional<User> findByUsername(String username);

    /**
     * Checks if a User with the given username already exists.
     * @param username The username to check.
     * @return True if a user with this username exists, false otherwise.
     */
    boolean existsByUsername(String username);

    /**
     * Checks if a User with the given full name already exists.
     * The `name` is also unique in our model.
     * @param name The full name to check.
     * @return True if a user with this name exists, false otherwise.
     */
    boolean existsByName(String name);
}