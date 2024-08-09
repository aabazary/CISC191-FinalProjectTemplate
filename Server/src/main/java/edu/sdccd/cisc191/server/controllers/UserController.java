package edu.sdccd.cisc191.server.controllers;

import edu.sdccd.cisc191.server.models.User;
import edu.sdccd.cisc191.server.config.JwtUtil;
import edu.sdccd.cisc191.server.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static edu.sdccd.cisc191.server.config.JwtUtil.generateToken;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/signup")
    public String signUp(@RequestBody User user) {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            return "Email already exists";
        }
        // Encode password before saving
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode(user.getPassword()));
        userRepository.save(user);
        return "User registered successfully";
    }

    // Example for a more detailed authentication setup, using JWT tokens
    @PostMapping("/login")
    public ResponseEntity<?> logIn(@RequestBody User loginRequest) {
        User existingUser = userRepository.findByEmail(loginRequest.getEmail());
        Map<String, String> response = new HashMap<>();

        if (existingUser != null) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            boolean passwordMatches = encoder.matches(loginRequest.getPassword(), existingUser.getPassword());

            if (passwordMatches) {
                String token = generateToken(existingUser.getEmail());
                response.put("token", token);
                response.put("message", "Login successful");
                return ResponseEntity.ok(response);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
    }



}
