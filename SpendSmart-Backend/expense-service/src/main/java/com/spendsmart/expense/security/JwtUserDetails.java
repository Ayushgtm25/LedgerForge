/**
 * JwtUserDetails stores additional user information from JWT tokens.
 *
 * This allows downstream services to access user identity without making
 * additional database calls.
 *
 * @author SpendSmart Development Team
 * @version 1.0
 */
package com.spendsmart.expense.security;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtUserDetails {
    private Long userId;
    private String email;
    private String role;
    private String subscriptionType;
}


