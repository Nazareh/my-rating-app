package com.turminaz.myratingapp.config;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFacade {
    public String authenticatedUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
