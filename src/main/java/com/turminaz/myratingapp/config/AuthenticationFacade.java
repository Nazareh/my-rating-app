package com.turminaz.myratingapp.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuthenticationFacade {
    private final FirebaseAuth firebaseAuth;

    public String getUserUid() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public boolean isAdmin() {
        var userId = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            Boolean isAdmin = (Boolean) firebaseAuth.getUser(userId).getCustomClaims().get("admin");

            return isAdmin != null && isAdmin;

        } catch (FirebaseAuthException e) {
            throw new RuntimeException(e);
        }
    }
}
