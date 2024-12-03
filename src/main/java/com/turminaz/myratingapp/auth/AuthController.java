package com.turminaz.myratingapp.auth;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.turminaz.myratingapp.config.IsAdmin;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final FirebaseAuth firebaseAuth;

    @PostMapping(path = "/user-claims/admin/{uid}")
    @IsAdmin
    public void giveAdminClaim(@PathVariable String uid) throws FirebaseAuthException {
        HashMap<String, Object> newClaims = new HashMap<>();
        newClaims.put("admin", true);
        firebaseAuth.setCustomUserClaims(uid, newClaims);
    }

}
