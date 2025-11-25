package com.backend.theatersnacks.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.token.Token;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpirationMs;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpirationMs;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateAccessToken(long userId, String phoneNumber, String role) {
        return generateToken(userId, phoneNumber,role, accessTokenExpirationMs);
    }

    public String generateRefreshToken(long userId, String phoneNumber, String role) {
        return generateToken(userId, phoneNumber,role, refreshTokenExpirationMs);
    }

    private String generateToken (
            long userId,
            String phoneNumber,
            String role,
            long expirationMs
    ) {
        Date current = new Date();
        Date expirationDate = new Date(current.getTime()+expirationMs);
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(current)
                .setExpiration(expirationDate)
                .claim("phone", phoneNumber)
                .claim("role", role)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(Token token) {
        try{
            Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws((CharSequence) token);
            return true;
        }
        catch (JwtException ex) {
            return  false;
        }
    }

    public Long getUserId(String token) {
        Claims claims = extractAllClaims(token);
        return Long.parseLong(claims.getSubject());
    }

    public String getPhoneNumber(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("phone", String.class);
    }

    public String getRole(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("role", String.class);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}
