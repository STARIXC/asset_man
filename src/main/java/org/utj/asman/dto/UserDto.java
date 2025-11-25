package org.utj.asman.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * DTO for creating or updating a User (Java 8 Compatible).
 * Uses standard Java class structure with Lombok and javax.validation.
 * The 'username' is expected to be an email address.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    /**
     * Unique identifier for the User. Null for create operations.
     */
    private Long id;

    /**
     * The unique identifier for login, validated as an email address.
     */
    @NotBlank(message = "Username is required.")
    @Email(message = "Username must be a valid email format.")
    @Size(max = 255, message = "Username cannot exceed 255 characters.")
    private String username;

    /**
     * The user's full name.
     */
    @NotBlank(message = "Full name is required.")
    @Size(max = 255, message = "Full name cannot exceed 255 characters.")
    private String name;

    /**
     * The password. Note: This should be handled securely by the service layer (e.g., hashed).
     */
    private String password;

    /**
     * The role of the user, e.g., "ROLE_ADMIN", "ROLE_USER".
     */
    @NotBlank(message = "User role is required.")
    private String role;

    /**
     * Account status. True if the user account is active.
     */
    private boolean enabled;
}