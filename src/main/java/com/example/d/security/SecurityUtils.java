// SecurityUtils.java - yangi klass, umumiy joyda
package com.example.d.security;

import com.example.d.exception.ForbiddenException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {
    public String getUsername(Authentication authentication) {
        if (!(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            throw new ForbiddenException("Invalid user");
        }
        return userDetails.getUsername();
    }
}