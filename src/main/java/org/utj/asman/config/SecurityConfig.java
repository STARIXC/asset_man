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

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configures the security filter chain using Spring Boot 2.7.x compatible syntax.
     * Defines specific access rules for /admin/ and /api/public/.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF for simpler API usage
                .httpBasic(withDefaults()) // Enable HTTP Basic authentication
                .authorizeRequests(auth -> auth // Spring Boot 2.7.x syntax
                        // 1. Admin-specific endpoints: requires 'ADMIN' role/authority
                        .antMatchers("/admin/**").hasAuthority("ADMIN")
                        // 2. Open endpoints (e.g. for initial setup or health check)
                        .antMatchers("/api/public/**").permitAll()
                        // 3. Secure everything else: requires any authenticated user
                        .anyRequest().authenticated()
                );
        return http.build();
    }

    /**
     * Defines the password encoder (BCrypt) used for securing user passwords.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures the Authentication Manager to use the custom UserDetailsService
     * and the PasswordEncoder defined above.
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http,
                                                       PasswordEncoder bCryptPasswordEncoder,
                                                       UserDetailsService userDetailService)
            throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailService)
                .passwordEncoder(bCryptPasswordEncoder)
                .and()
                .build();
    }
}