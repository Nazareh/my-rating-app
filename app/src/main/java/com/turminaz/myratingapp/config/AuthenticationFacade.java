package com.turminaz.myratingapp.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticationFacade {
    private final FirebaseAuth firebaseAuth;
    public UserRecord authenticatedUser() {
        var userId = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
           return firebaseAuth.getUser(userId);

        } catch (FirebaseAuthException e) {
            throw new RuntimeException(e);
        }

    }
}
