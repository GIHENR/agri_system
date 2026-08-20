package com.agri.agri.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disabled for API testing
                .authorizeHttpRequests(auth -> auth
                        // Public pages
                        .requestMatchers("/login", "/register", "/verify-email", "/request-reset", "/reset-password", "/css/**", "/images/**", "/js/**", "/error").permitAll()

                        // Admin-only routes
                        .requestMatchers("/admin/**", "/farmer_management", "/buyer_management", "/reports").hasRole("ADMIN")

                        // Buyer-only routes
                        .requestMatchers("/order", "/order_history", "/api/orders/**").hasRole("BUYER")

                        // Farmer-only routes
                        .requestMatchers("/order_m", "/crop_inventory", "/api/inventory/**").hasRole("FARMER")

                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler((request, response, authentication) -> {
                            boolean isAdmin = authentication.getAuthorities().stream()
                                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
                            boolean isFarmer = authentication.getAuthorities().stream()
                                    .anyMatch(a -> a.getAuthority().equals("ROLE_FARMER"));

                            if (isAdmin) {
                                response.sendRedirect("/farmer_management");
                            } else if (isFarmer) {
                                response.sendRedirect("/order_m");
                            } else {
                                response.sendRedirect("/order");
                            }
                        })
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}