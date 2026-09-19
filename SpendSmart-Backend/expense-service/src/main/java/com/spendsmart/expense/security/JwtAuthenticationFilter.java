/**
 * JwtAuthenticationFilter intercepts HTTP requests and validates JWT tokens.
 *
 * This filter:
 * 1. Extracts the JWT token from the "Authorization: Bearer <token>" header
 * 2. Validates the token's signature and expiration
 * 3. Extracts user identity and role claims
 * 4. Populates the SecurityContext with the authenticated user
 *
 * @author SpendSmart Development Team
 * @version 1.0
 */
package com.spendsmart.expense.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            // Extract token from Authorization header (format: "Bearer <token>")
            String token = extractTokenFromRequest(request);

            if (token != null && jwtUtils.validateToken(token)) {
                Long userId = jwtUtils.getUserIdFromToken(token);
                String email = jwtUtils.getEmailFromToken(token);
                String role = jwtUtils.getRoleFromToken(token);
                String subscriptionType = jwtUtils.getSubscriptionTypeFromToken(token);

                // Create authentication object with role authority
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                email,
                                null,
                                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + role))
                        );

                // Store additional user info for downstream use
                authentication.setDetails(new JwtUserDetails(userId, email, role, subscriptionType));

                // Set the authentication in the SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.debug("JWT token validated for user: {} (userId: {})", email, userId);
            }
        } catch (Exception ex) {
            log.warn("JWT filter processing failed: {}", ex.getMessage());
        }

        // Continue with the filter chain
        filterChain.doFilter(request, response);
    }

    /**
     * Extract JWT token from the Authorization header.
     * Expected format: "Bearer <token>"
     *
     * @param request the HTTP request
     * @return the token string, or null if not present or invalid format
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }
}


