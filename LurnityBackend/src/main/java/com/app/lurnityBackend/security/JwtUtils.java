package com.app.lurnityBackend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Base64;

@Component
public class JwtUtils {

    private final SecretKey jwtSecret = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    private final long jwtExpirationMs = 3600000; // 1 hour

    /**
     * Generate JWT token with email and role
     */
    public String generateToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(jwtSecret)
                .compact();
    }

    /**
     * Extract email (subject)
     */
    public String getEmailFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    /**
     * Extract role from token
     */
    public String getRoleFromToken(String token) {
        return (String) getClaimsFromToken(token).get("role");
    }

    /**
     * Validate token expiration
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    private Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtSecret)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Optional: Base64 key string
     */
    public String getBase64Secret() {
        return Base64.getEncoder().encodeToString(jwtSecret.getEncoded());
    }
}
