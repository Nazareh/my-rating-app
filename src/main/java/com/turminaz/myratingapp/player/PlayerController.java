package com.turminaz.myratingapp.player;

import com.google.firebase.auth.FirebaseAuthException;
import com.netflix.dgs.codegen.generated.types.PlayerResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PlayerController {

    private final PlayerService playerService;

    @MutationMapping
    PlayerResponse onboardMyself(Principal principal) throws FirebaseAuthException {
        return playerService.onboardPlayer(principal.getName());
    }
}
