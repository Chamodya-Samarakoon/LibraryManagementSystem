package com.example.library.controller;

import com.example.library.dto.LoginRequest;
import com.example.library.dto.SignupRequest;
import com.example.library.dto.JwtResponse;
import com.example.library.model.Role;
import com.example.library.model.User;
import com.example.library.repository.UserRepository;
import com.example.library.security.JwtUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthController(UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    // ------------------------------
    // SIGNUP: Register a new user
    // ------------------------------
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {

        // Check if username already exists
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body("Username already exists");
        }

        // Ensure role has ROLE_ prefix
        String roleName = request.getRole().name();
        if (!roleName.startsWith("ROLE_")) {
            roleName = "ROLE_" + roleName;
        }

        User user = new User(
                request.getUsername(),
                passwordEncoder.encode(request.getPassword()),
                Role.valueOf(roleName));

        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

    // ------------------------------
    // SIGNIN: Login user and return JWT
    // ------------------------------
    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody LoginRequest request) {

        User user = userRepository.findByUsername(request.getUsername())
                .orElse(null);

        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(401).body("Invalid username or password");
        }

        String token = jwtUtils.generateToken(user.getUsername());
        return ResponseEntity.ok(new JwtResponse(token));
    }
}
