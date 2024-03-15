package com.turminaz.myratingapp.match;

import com.netflix.dgs.codegen.generated.types.MatchInput;
import com.netflix.dgs.codegen.generated.types.MatchStatus;
import com.turminaz.myratingapp.config.RabbitConfig;
import com.turminaz.myratingapp.match.domain.Match;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class MatchService {

    private final MatchMapper matchMapper;
    private final RabbitTemplate rabbitTemplate;

    public void createMatch(MatchInput input) {
        log.info("Creating match: {}", input);
        var mappedMatch = matchMapper.toMatch(input);

        var matchId = UUID.randomUUID();
        mappedMatch.setId(matchId.toString());

        mappedMatch.getTeam1().getPlayer1().setName("Team 1 Player 1");
        mappedMatch.getTeam1().getPlayer1().setMatchStatus(MatchStatus.APPROVED);
        mappedMatch.getTeam1().getPlayer2().setMatchStatus(MatchStatus.PENDING);
        mappedMatch.getTeam1().getPlayer2().setName("Team 1 Player 2");
        mappedMatch.getTeam2().getPlayer1().setMatchStatus(MatchStatus.PENDING);
        mappedMatch.getTeam2().getPlayer1().setName("Team 2 Player 1");
        mappedMatch.getTeam2().getPlayer2().setMatchStatus(MatchStatus.PENDING);
        mappedMatch.getTeam2().getPlayer2().setName("Team 2 Player 2");

        log.info("Sending match {}", mappedMatch.getId());

        rabbitTemplate
                .convertAndSend(RabbitConfig.MATCH_EXCHANGE,
                        "match.created",
                        new Match(matchId, mappedMatch.getStartTime()));

    }
}
