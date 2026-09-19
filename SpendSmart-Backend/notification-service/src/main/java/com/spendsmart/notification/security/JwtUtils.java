package com.spendsmart.notification.security;

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

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            log.warn("JWT validation failed: {}", ex.getMessage());
            return false;
        }
    }

    public Claims getClaimsFromToken(String token) {
        return Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token).getPayload();
    }

    /**
     * JJWT may deserialize numeric claims as Integer; {@link Claims#get(String, Class)} with Long.class
     * then returns null. Coerce any Number to Long.
     */
    public Long getUserIdFromToken(String token) {
        return parseUserId(getClaimsFromToken(token));
    }

    public String getEmailFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    public String getRoleFromToken(String token) {
        Object role = getClaimsFromToken(token).get("role");
        return role != null ? String.valueOf(role) : null;
    }

    public String getSubscriptionTypeFromToken(String token) {
        Object st = getClaimsFromToken(token).get("subscriptionType");
        return st != null ? String.valueOf(st) : "NORMAL";
    }

    private static Long parseUserId(Claims claims) {
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

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }
}
