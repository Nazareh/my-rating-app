package com.turminaz.myratingapp.match;

import com.netflix.dgs.codegen.generated.types.MatchInput;
import com.netflix.dgs.codegen.generated.types.MatchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
class MatchController {

    private final MatchService matchService;

    @MutationMapping
    MatchResponse postMatch(@Argument MatchInput input)  {
        return matchService.createMatch(input);
    }

    @MutationMapping
    MatchResponse approveMatch(@Argument String matchId)  {
        return matchService.approveMatch(matchId);
    }

    @MutationMapping
    MatchResponse rejectMatch(@Argument String matchId)  {
        return matchService.rejectMatch(matchId);
    }
}
