package com.app.lurnityBackend.service;

import com.app.lurnityBackend.model.RefreshToken;
import com.app.lurnityBackend.model.User;
import com.app.lurnityBackend.repository.RefreshTokenRepository;
import com.app.lurnityBackend.repository.UserRepo;
import com.app.lurnityBackend.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepo userRepo;
    private final JwtUtils jwtUtils;

    // Create new refresh token
    public String createRefreshToken(String userId) {
        String token = UUID.randomUUID().toString();

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUserId(userId);
        refreshToken.setToken(token);
        refreshToken.setExpiryDate(Instant.now().plus(7, ChronoUnit.DAYS));

        refreshTokenRepository.save(refreshToken);

        return token;
    }

    // ------------------- REFRESH ACCESS TOKEN -------------------
    public String refreshAccessToken(String refreshToken) {
        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid refresh token"));

        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Refresh token expired");
        }

        // Directly fetch user by ID
        User user = userRepo.findById(token.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return jwtUtils.generateToken(user.getEmail(), String.valueOf(user.getRole()));
    }


    // Find refresh token
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    // Delete a single token (logout from one device)
    public void deleteByToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }

    // Delete all tokens for a user (logout everywhere)
    public void deleteAllByUserId(String userId) {
        refreshTokenRepository.deleteAllByUserId(userId);
    }
}
