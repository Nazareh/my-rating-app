package com.turminaz.myratingapp.auth;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final FirebaseAuth firebaseAuth;

    @PostMapping(path = "/user-claims/{uid}/{authorityToAdd}")
    public void addAuthority(@PathVariable String uid, @PathVariable Authority authorityToAdd)
            throws FirebaseAuthException {

//        Map<String, Object> currentClaims = firebaseAuth.getUser(uid).getCustomClaims();
//
//        ArrayList<Authority> rolesOld = (ArrayList<Authority>) currentClaims.getOrDefault("authorities", List.of());
//        Set<Authority> rolesNew = new HashSet<>(rolesOld);
//        rolesNew.add(authorityToAdd);

        HashMap<String, Object> newClaims = new HashMap<>();
//        newClaims.put("authorities", List.of(authorityToAdd.name()));
        firebaseAuth.setCustomUserClaims(uid, newClaims);
    }

}
