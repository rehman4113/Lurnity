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
    private final TokenService tokenService; // Injected properly

    // ------------------- SIGNUP -------------------
    public SignupResponse signup(SignupRequest request) {
        // 1. Check if user already exists
        if (userRepo.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists!");
        }

        // 2. Create new user
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        // 3. Save to DB
        User savedUser = userRepo.save(user);

        // 4. Generate tokens
        String accessToken = jwtUtils.generateToken(savedUser.getEmail(), String.valueOf(savedUser.getRole()));
        String refreshToken = tokenService.createRefreshToken(savedUser.getId());

        // 5. Return response
        return new SignupResponse(
                savedUser.getId(),
                savedUser.getFirstName(),
                savedUser.getLastName(),
                savedUser.getEmail(),
                savedUser.getRole(),
                accessToken,
                refreshToken,
                "Signup successful"
        );
    }

    // ------------------- LOGIN -------------------
    public LoginResponse login(LoginRequest request) {
        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        // Generate access token using email + role
        String accessToken = jwtUtils.generateToken(user.getEmail(), String.valueOf(user.getRole()));

        // Generate refresh token stored in DB
        String refreshToken = tokenService.createRefreshToken(user.getId());

        return new LoginResponse(
                "Login successful",
                accessToken,
                refreshToken,
                user.getEmail()
        );
    }

    // ------------------- GET PROFILE -------------------
    public SignupResponse getUserProfile(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        String email = jwtUtils.getEmailFromToken(token);

        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new SignupResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole(),
                null,
                null,
                "Profile fetched successfully"
        );
    }

    // ------------------- LOGOUT -------------------
    public void logout(String refreshToken) {
        tokenService.deleteByToken(refreshToken);
    }
}
