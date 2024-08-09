package edu.sdccd.cisc191.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF protection
                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers("/api/users/signup", "/api/users/login","/api/characters" ).permitAll() // Allow signup and login without authentication
//                                .requestMatchers("/api/characters").authenticated() // Require authentication for other endpoints
                                .anyRequest().denyAll() // Deny all other requests
                )
                .formLogin(withDefaults()); // Use default form login

        return http.build();
    }
}