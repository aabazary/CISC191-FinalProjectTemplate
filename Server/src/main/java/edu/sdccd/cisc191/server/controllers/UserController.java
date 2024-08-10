package edu.sdccd.cisc191.server.controllers;

import edu.sdccd.cisc191.server.models.User;
import edu.sdccd.cisc191.server.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import static edu.sdccd.cisc191.server.config.JwtUtil.generateToken;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    /**Create a User
     * @route  /api/users/signup
     * @param user Email and Password required
     */
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

    /** Login User
     * @route  /api/users/login
     * @param loginRequest Email and Password required
     */
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
                response.put("userId", String.valueOf(existingUser.getId()));
                response.put("message", "Login successful");
                return ResponseEntity.ok(response);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
    }

    /**Get All Users
     * @route /api/users
     */
    @GetMapping
    public List<User> getAllUsers() {
        Iterable<User> usersIterable = userRepository.findAll();
        List<User> userList = new ArrayList<>();
        usersIterable.forEach(userList::add);
        return userList;
    }

    /** Get User by ID
     * @route /api/users/:id
     * @param id User's Id
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    /**Update a User by ID
     * @route /api/users/:id
     * @param id User Id
     * @param user Updated User object
     */
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User existingUser = userOptional.get();

            existingUser.setEmail(user.getEmail());
            existingUser.setPassword(user.getPassword());
            existingUser.setClaimedCharacter(user.getClaimedCharacter());
            existingUser.setPlaying(user.isPlaying());
            userRepository.save(existingUser);
            return ResponseEntity.ok(existingUser);

        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }



}
