package com.app.lurnityBackend.controller;

import com.app.lurnityBackend.dto.LoginRequest;
import com.app.lurnityBackend.dto.LoginResponse;
import com.app.lurnityBackend.dto.SignupRequest;
import com.app.lurnityBackend.dto.SignupResponse;
import com.app.lurnityBackend.service.AuthService;
import com.app.lurnityBackend.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final TokenService tokenService;

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

    // ------------------- REFRESH ACCESS TOKEN -------------------
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        Map<String, String> response = new HashMap<>();
        try {
            String newAccessToken = tokenService.refreshAccessToken(refreshToken);
            response.put("accessToken", newAccessToken);
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException e) {
            response.put("message", e.getReason());
            return ResponseEntity.status(e.getStatusCode()).body(response);
        }
    }

    // ------------------- LOGOUT -------------------
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        try {
            authService.logout(refreshToken);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Logged out successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Failed to logout");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // ------------------- LOGOUT ALL DEVICES -------------------
    @PostMapping("/logout-all")
    public ResponseEntity<Map<String, String>> logoutAll(@RequestBody Map<String, String> request) {
        String userId = request.get("userId"); // or extract from access token
        try {
            tokenService.deleteAllByUserId(userId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Logged out from all devices successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Failed to logout from all devices");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<SignupResponse> getMyProfile(@RequestHeader("Authorization") String authHeader) {
        try {
            SignupResponse user = authService.getUserProfile(authHeader);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            // Return a SignupResponse with error message
            SignupResponse errorResponse = new SignupResponse(
                    null, // id
                    null, // firstName
                    null, // lastName
                    null, // email
                    null, // role
                    null,
                    null,
                    "Invalid token or user not found"
            );
            return ResponseEntity.status(401).body(errorResponse);
        }
    }

}