package com.turminaz.myratingapp.match;

import com.google.firebase.auth.FirebaseAuthException;
import com.netflix.dgs.codegen.generated.types.MatchResponse;
import com.netflix.dgs.codegen.generated.types.MatchInput;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@Slf4j
class MatchController {

    private final MatchService matchService;

    @MutationMapping
    MatchResponse postMatch(@Argument MatchInput input, Principal principal)  {
        return matchService.createMatch(input, principal);
    }
}
