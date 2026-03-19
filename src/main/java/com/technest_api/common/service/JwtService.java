package com.technest_api.common.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String secretKey;

    @Value("${app.jwt.access-token-expiration-ms}")
    private long accessTokenExpirationMs;

    @Value("${app.jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpirationMs;

    public String generateAccessToken(String userId, String email, String role) {
        return buildToken(userId, email, role, accessTokenExpirationMs);
    }

    public String generateRefreshToken(String userId, String email, String role) {
        return buildToken(userId, email, role, refreshTokenExpirationMs);
    }

    public boolean isTokenValid(String token) {
        try {
            parseClaims(token);
            return true;
        }
        catch (JwtException e) {
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            parseClaims(token);
            return false;
        }
        catch (ExpiredJwtException e) {
            return true;
        }
        catch (JwtException e) {
            return false;
        }
    }

    public String extractUserIdFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    public String extractEmailFromToken(String token) {
        return parseClaims(token).get("email", String.class);
    }

    public String extractRoleFromToken(String token) {
        return parseClaims(token).get("role", String.class);
    }

    private String buildToken(String userId, String email, String role, long expiration) {
        return Jwts.builder()
                .subject(userId)
                .claim("email", email)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    private Claims parseClaims(String token) {
        try {
            return (Claims) Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        }
        catch (ExpiredJwtException e) {
            throw e;    // ← rethrow so callers can still catch ExpiredJwtException specifically
        }
        catch (JwtException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }
    }


    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

}
