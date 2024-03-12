package com.turminaz.myratingapp.Match;

import com.netflix.dgs.codegen.generated.types.Match;
import com.netflix.dgs.codegen.generated.types.MatchInput;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MatchController {

    private final MatchService matchService;

    @MutationMapping
    public Match postMatch(@Argument MatchInput input) {

        return matchService.createMatch(input);
    }
}
