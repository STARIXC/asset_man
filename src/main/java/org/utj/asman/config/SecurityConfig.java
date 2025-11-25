package org.utj.asman.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * Spring Security Configuration for Spring Boot 2.7.x (Spring Security 5.x)
 * and guaranteed Java 8 compatibility.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configures the security filter chain.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic(withDefaults()) // Enable HTTP Basic authentication (for API)
                .authorizeRequests(auth -> auth // Spring Security 5.x syntax (Java 8 compatible)
                        .antMatchers("/admin/**").hasRole("ADMIN")
                        // Allow access to login page and static assets
                        .antMatchers("/login", "/css/**", "/js/**", "/assets/**").permitAll()
                        .antMatchers("/api/public/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login") // The URL to show the login form (GET)
                        .loginProcessingUrl("/login") // The URL to submit the login form to (POST) - CRITICAL FIX
                        .defaultSuccessUrl("/admin/dashboard", true) // Redirect here after success
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );
        return http.build();
    }

    /**
     * Defines the standard BCrypt Password Encoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the Authentication Manager using the custom UserDetailsService.
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http,
                                                       PasswordEncoder bCryptPasswordEncoder,
                                                       UserDetailsService userDetailService)
            throws Exception {
        // Standard Java 8/Spring Security 5.x builder pattern
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailService)
                .passwordEncoder(bCryptPasswordEncoder)
                .and()
                .build();
    }
}