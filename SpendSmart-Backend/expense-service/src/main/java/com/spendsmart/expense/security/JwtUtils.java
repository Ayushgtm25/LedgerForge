/**
 * JwtUtils provides utility methods for JWT validation and claim extraction.
 *
 * This class validates tokens issued by the auth-service and extracts user
 * identity information from JWT claims.
 *
 * @author SpendSmart Development Team
 * @version 1.0
 */
package com.spendsmart.expense.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
@Slf4j
public class JwtUtils {

    @Value("${jwt.secret}")
    private String jwtSecret;

    /**
     * Validate a JWT token's signature and expiration.
     *
     * @param token the JWT string to validate
     * @return true if the token is valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            log.warn("JWT validation failed: {}", ex.getMessage());
            return false;
        }
    }

    /**
     * Extract claims from a JWT token.
     *
     * @param token the JWT string
     * @return Claims object containing all claims in the token
     */
    public Claims getClaimsFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Extract the userId claim from a JWT token.
     *
     * @param token the JWT string
     * @return the userId as Long
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        Object raw = claims.get("userId");
        if (raw == null) {
            return null;
        }
        if (raw instanceof Number) {
            return ((Number) raw).longValue();
        }
        try {
            return Long.parseLong(String.valueOf(raw));
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    /**
     * Extract the email (subject) from a JWT token.
     *
     * @param token the JWT string
     * @return the email address
     */
    public String getEmailFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getSubject();
    }

    /**
     * Extract the role claim from a JWT token.
     *
     * @param token the JWT string
     * @return the role (USER or ADMIN)
     */
    public String getRoleFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        Object role = claims.get("role");
        return role != null ? String.valueOf(role) : null;
    }

    /**
     * Extract the subscriptionType claim from a JWT token.
     *
     * @param token the JWT string
     * @return the subscription type (NORMAL or PAID)
     */
    public String getSubscriptionTypeFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        Object st = claims.get("subscriptionType");
        return st != null ? String.valueOf(st) : "NORMAL";
    }
}


