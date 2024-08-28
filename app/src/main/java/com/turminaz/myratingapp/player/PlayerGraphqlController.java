package com.turminaz.myratingapp.player;

import com.netflix.dgs.codegen.generated.types.PlayerResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PlayerGraphqlController {

    private final PlayerService playerService;

    @MutationMapping
    PlayerResponse onboardMyself(Principal principal) {
        return playerService.onboardPlayer(principal.getName());
    }
}
