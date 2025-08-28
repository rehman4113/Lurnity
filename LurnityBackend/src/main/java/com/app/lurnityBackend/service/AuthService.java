package com.app.lurnityBackend.service;

import com.app.lurnityBackend.dto.LoginRequest;
import com.app.lurnityBackend.dto.LoginResponse;
import com.app.lurnityBackend.dto.SignupRequest;
import com.app.lurnityBackend.dto.SignupResponse;
import com.app.lurnityBackend.model.User;
import com.app.lurnityBackend.repository.UserRepo;
import com.app.lurnityBackend.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public SignupResponse signup(SignupRequest request) {
        // 1. Check if user already exists
        if (userRepo.existsByEmail(request.getEmail())) {
            // Throw exception with 409 Conflict
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email already exists!"
            );
        }

        // 2. Create new user
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // ⚠️ hash this in real apps!
        user.setRole(request.getRole());
        // 3. Save to DB
        User savedUser = userRepo.save(user);

        // 4. Return response
        return new SignupResponse(
                savedUser.getId(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getEmail(),
                savedUser.getRole(),
                "Signup successful"
        );
    }

    // ------------------- LOGIN -------------------
    public LoginResponse login(LoginRequest request) {
        // 1. Find user by email
        Optional<User> userOptional = userRepo.findByEmail(request.getEmail());

        if (userOptional.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid email or password"
            );
        }

        User user = userOptional.get();

        // 2. Check password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        // 3. Generate JWT token based on email
        String token = jwtUtils.generateToken(user.getEmail(), String.valueOf(user.getRole()));

        // 4. Return login response with token
        return new LoginResponse(
                "Login successful",
                token,
                user.getEmail()
        );
    }

}
