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
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers("/api/users/signup", "/api/users/login", "/api/characters", "/api/characters/*","/api/users/*","/api/users","/api/characters/claim").permitAll()
//                                .requestMatchers("/api/characters").authenticated() // Require authentication for other endpoints Not Working ATM
                                .anyRequest().denyAll() // Deny all other requests
                )
                .formLogin(withDefaults());

        return http.build();
    }
}