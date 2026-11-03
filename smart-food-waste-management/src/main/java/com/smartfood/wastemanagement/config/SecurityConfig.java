package com.smartfood.wastemanagement.config;

import com.smartfood.wastemanagement.repository.AppUserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService userDetailsService(AppUserRepository users) {
        return email -> users.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(
                a -> a.requestMatchers("/", "/login", "/register/**", "/css/**", "/js/**", "/uploads/**", "/error")
                        .permitAll().requestMatchers("/admin/**").hasRole("ADMIN").requestMatchers("/restaurant/**")
                        .hasRole("RESTAURANT").requestMatchers("/ngo/**").hasRole("NGO").anyRequest().authenticated())
                .formLogin(f -> f.loginPage("/login").defaultSuccessUrl("/dashboard", true).permitAll())
                .logout(l -> l.logoutSuccessUrl("/login?logout").permitAll())
                .exceptionHandling(e -> e.accessDeniedPage("/access-denied"));
        return http.build();
    }
}