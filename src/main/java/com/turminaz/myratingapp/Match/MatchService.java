package com.turminaz.myratingapp.Match;

import com.netflix.dgs.codegen.generated.types.Match;
import com.netflix.dgs.codegen.generated.types.MatchInput;
import com.netflix.dgs.codegen.generated.types.MatchStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jeasy.random.EasyRandom;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class MatchService {

    private final MatchMapper matchMapper;
    public Match createMatch(MatchInput input) {
        log.info("Creating match: {}", input);

        var mappedMatch = matchMapper.toMatch(input);
        mappedMatch.setId(UUID.randomUUID().toString());
        mappedMatch.getTeam1().getPlayer1().setName("Team 1 Player 1");
        mappedMatch.getTeam1().getPlayer1().setMatchStatus(MatchStatus.APPROVED);
        mappedMatch.getTeam1().getPlayer2().setMatchStatus(MatchStatus.PENDING);
        mappedMatch.getTeam1().getPlayer2().setName("Team 1 Player 2");
        mappedMatch.getTeam2().getPlayer1().setMatchStatus(MatchStatus.PENDING);
        mappedMatch.getTeam2().getPlayer1().setName("Team 2 Player 1");
        mappedMatch.getTeam2().getPlayer2().setMatchStatus(MatchStatus.PENDING);
        mappedMatch.getTeam2().getPlayer2().setName("Team 2 Player 2");

        return mappedMatch;
    }
}
