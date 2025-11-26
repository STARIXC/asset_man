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
                                .httpBasic(withDefaults())
                                .authorizeRequests(auth -> auth
                                                // Admin-only endpoints (write operations)
                                                .antMatchers("/admin/users/**").hasRole("ADMIN")
                                                .antMatchers("/admin/facilities/delete/**").hasRole("ADMIN")
                                                .antMatchers("/admin/facilities/update/**").hasRole("ADMIN")
                                                .antMatchers("/admin/facilities/save").hasRole("ADMIN")
                                                .antMatchers("/admin/counties/delete/**").hasRole("ADMIN")
                                                .antMatchers("/admin/counties/update/**").hasRole("ADMIN")
                                                .antMatchers("/admin/counties/save").hasRole("ADMIN")
                                                .antMatchers("/admin/assets/delete/**").hasRole("ADMIN")
                                                .antMatchers("/admin/assets/update/**").hasRole("ADMIN")
                                                .antMatchers("/admin/assets/save").hasRole("ADMIN")
                                                .antMatchers("/admin/cpu-specs/delete/**").hasRole("ADMIN")
                                                .antMatchers("/admin/cpu-specs/update/**").hasRole("ADMIN")
                                                .antMatchers("/admin/cpu-specs/save").hasRole("ADMIN")

                                                // Admin and User can view (read operations)
                                                .antMatchers("/admin/dashboard").hasAnyRole("ADMIN", "USER")
                                                .antMatchers("/admin/facilities").hasAnyRole("ADMIN", "USER")
                                                .antMatchers("/admin/facilities/get/**").hasAnyRole("ADMIN", "USER")
                                                .antMatchers("/admin/facilities/pdf/**").hasAnyRole("ADMIN", "USER")
                                                .antMatchers("/admin/counties").hasAnyRole("ADMIN", "USER")
                                                .antMatchers("/admin/counties/get/**").hasAnyRole("ADMIN", "USER")
                                                .antMatchers("/admin/assets").hasAnyRole("ADMIN", "USER")
                                                .antMatchers("/admin/assets/get/**").hasAnyRole("ADMIN", "USER")
                                                .antMatchers("/admin/cpu-specs").hasAnyRole("ADMIN", "USER")
                                                .antMatchers("/admin/cpu-specs/get/**").hasAnyRole("ADMIN", "USER")

                                                // Catch-all for other admin endpoints
                                                .antMatchers("/admin/**").hasAnyRole("ADMIN", "USER")

                                                // Public access
                                                .antMatchers("/login", "/css/**", "/js/**", "/assets/**", "/images/**")
                                                .permitAll()
                                                .antMatchers("/api/public/**").permitAll()
                                                .anyRequest().authenticated())
                                .formLogin(form -> form
                                                .loginPage("/login")
                                                .loginProcessingUrl("/login")
                                                .defaultSuccessUrl("/admin/dashboard", true)
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/login?logout")
                                                .permitAll());
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