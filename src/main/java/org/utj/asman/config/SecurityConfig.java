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
 * Spring Security Configuration with Role-Based Access Control
 * - ADMIN: Full access (create, read, update, delete)
 * - USER: Read-only access (view and download PDFs)
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                        // 1. Disable CSRF for API endpoints to allow Mobile App POST/PUT requests
                        // We keep it enabled for the web interface (default behavior for non-API paths) or disable globally if acceptable for this internal tool.
                        // For simplicity and to fix the 403 error on mobile PUTs, we disable it globally or ignore specific paths.
                        .csrf().ignoringAntMatchers("/api/**", "/login", "/logout")
                        .and()

                        // 2. Enable HTTP Basic for API (Mobile App)
                        .httpBasic(withDefaults())

                        // 3. Authorization Rules
                        .authorizeRequests(auth -> auth
                                // --- ADMIN ONLY (Write Operations) ---
                                // User Management
                                .antMatchers("/admin/users/**").hasRole("ADMIN")
                                // Modification Endpoints (Create/Update/Delete)
                                .antMatchers("/admin/facilities/delete/**", "/admin/facilities/update/**", "/admin/facilities/save").hasRole("ADMIN")
                                .antMatchers("/admin/assets/delete/**", "/admin/assets/update/**", "/admin/assets/save").hasRole("ADMIN")
                                .antMatchers("/admin/cpu-specs/delete/**", "/admin/cpu-specs/update/**", "/admin/cpu-specs/save").hasRole("ADMIN")
                                .antMatchers("/admin/counties/delete/**", "/admin/counties/update/**", "/admin/counties/save").hasRole("ADMIN")
                                .antMatchers("/admin/settings/**").hasRole("ADMIN")

                                // --- SHARED ACCESS (Read Operations) ---
                                // Dashboard & Lists
                                .antMatchers("/admin/dashboard", "/admin/facilities", "/admin/assets", "/admin/counties", "/admin/cpu-specs").hasAnyRole("ADMIN", "USER")
                                // Read-only details & Exports
                                .antMatchers("/admin/facilities/get/**", "/admin/facilities/pdf/**").hasAnyRole("ADMIN", "USER")
                                .antMatchers("/admin/assets/get/**").hasAnyRole("ADMIN", "USER")
                                .antMatchers("/admin/cpu-specs/get/**").hasAnyRole("ADMIN", "USER")

                                // --- API ACCESS ---
                                // Allow any authenticated user (Admin or Standard) to use the Mobile App API
                                .antMatchers("/api/**").authenticated()

                                // --- PUBLIC ACCESS ---
                                // Login page and static assets (CSS, JS, Images)
                                .antMatchers("/login", "/css/**", "/js/**", "/assets/**", "/images/**").permitAll()
                                .antMatchers("/api/public/**").permitAll()

                                // Catch-all: Everything else requires at least authentication
                                .anyRequest().authenticated()
                        )

                        // 4. Form Login Configuration
                        .formLogin(form -> form
                                .loginPage("/login") // Custom login page URL
                                .loginProcessingUrl("/login") // URL to submit the login form
                                .defaultSuccessUrl("/admin/dashboard", true) // Redirect here after success
                                .permitAll()
                        )

                        // 5. Logout Configuration
                        .logout(logout -> logout
                                .logoutUrl("/logout")
                                .logoutSuccessUrl("/login?logout")
                                .permitAll()
                        );

                return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

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