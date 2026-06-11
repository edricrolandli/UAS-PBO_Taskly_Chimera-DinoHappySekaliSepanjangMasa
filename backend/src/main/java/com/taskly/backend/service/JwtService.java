package com.taskly.backend.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import java.util.Date;
import javax.crypto.SecretKey;

@Service
public class JwtService {

    private final String SECRET_STRING = "taskly-secret-key-super-secure-lts-2025-minimum-256-bits";
    private final SecretKey key = Keys.hmacShaKeyFor(SECRET_STRING.getBytes());

    public String generateToken(String email, String role) {
        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 86400000)) // Berlaku 1 hari
                .signWith(key)
                .compact();
    }

    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, String email) {
        return (extractEmail(token).equals(email) && !getClaims(token).getExpiration().before(new Date()));
    }

    private Claims getClaims(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }
}
