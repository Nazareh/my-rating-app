package com.turminaz.myratingapp.match;

import com.netflix.dgs.codegen.generated.types.MatchInput;
import com.netflix.dgs.codegen.generated.types.MatchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
class MatchService {

    private final MatchMapper mapper;
    private final RabbitTemplate rabbitTemplate;

    MatchResponse createMatch(MatchInput input) {
        log.info("Creating match: {}", input);

        var player1 = findPlayerById(input.getTeam1().getMatchPlayer1());
        var player2 = findPlayerById(input.getTeam1().getMatchPlayer2());
        var player3 = findPlayerById(input.getTeam2().getMatchPlayer1());
        var player4 = findPlayerById(input.getTeam2().getMatchPlayer2());

        var savedMatch = mapper.toMatch(UUID.randomUUID().toString(), input, player1, player2, player3, player4);

        rabbitTemplate.convertAndSend(MatchRabbitConfig.MATCH_EXCHANGE, MatchRabbitConfig.MATCH_QUEUE, savedMatch);

        return mapper.toMatchResponse(savedMatch);
    }

    MatchPlayer findPlayerById(String id){
        return new MatchPlayer(id, "Player", MatchStatus.PENDING);
    }

}
