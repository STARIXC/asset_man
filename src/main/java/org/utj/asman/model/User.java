package org.utj.asman.model;

import lombok.Data;
import javax.persistence.*;

@Entity
@Data
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String password; // Will store BCrypt hashed password

    @Column(nullable = false)
    private String role; // e.g., "ROLE_USER", "ROLE_ADMIN"
    
    private boolean enabled = true;
}