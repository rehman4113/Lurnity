package com.app.lurnityBackend.controller;

import com.app.lurnityBackend.dto.LoginRequest;
import com.app.lurnityBackend.dto.LoginResponse;
import com.app.lurnityBackend.dto.SignupRequest;
import com.app.lurnityBackend.dto.SignupResponse;
import com.app.lurnityBackend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // ✅ Signup endpoint
    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@RequestBody SignupRequest request) {
        SignupResponse response = authService.signup(request);
        return ResponseEntity.ok(response);
    }

    // ---------------- LOGIN ----------------
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
            // Call AuthService login method
            LoginResponse response = authService.login(request);
            return ResponseEntity.ok(response);

    }

    @GetMapping("/users")
    public String getAllUsers() {
        return "Successfully checked";
    }
}