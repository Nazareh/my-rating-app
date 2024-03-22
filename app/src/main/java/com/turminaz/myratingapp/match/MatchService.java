package com.turminaz.myratingapp.match;

import com.netflix.dgs.codegen.generated.types.MatchInput;
import com.netflix.dgs.codegen.generated.types.MatchResponse;
import com.turminaz.myratingapp.config.AuthenticationFacade;
import com.turminaz.myratingapp.player.PlayerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
class MatchService {

    private final MatchRepository repository;
    private final PlayerService playerService;
    private final AuthenticationFacade authenticationFacade;
    private final MatchMapper mapper;

    //    private final RabbitTemplate rabbitTemplate;

    MatchResponse createMatch(MatchInput input) {


        log.info("Creating match: {}", input);

        var authenticatedUserId = authenticationFacade.authenticatedUserId();
        var player1 = getPlayerOrOnboardPlayer(input.getTeam1().getMatchPlayer1(),
                authenticatedUserId.equals(input.getTeam1().getMatchPlayer1()));
        var player2 = getPlayerOrOnboardPlayer(input.getTeam1().getMatchPlayer2(),
                authenticatedUserId.equals(input.getTeam1().getMatchPlayer2()));
        var player3 = getPlayerOrOnboardPlayer(input.getTeam2().getMatchPlayer1(),
                authenticatedUserId.equals(input.getTeam2().getMatchPlayer1()));
        var player4 = getPlayerOrOnboardPlayer(input.getTeam2().getMatchPlayer2(),
                authenticatedUserId.equals(input.getTeam2().getMatchPlayer2()));

        var savedMatch = repository.save
                (mapper.toMatch(UUID.randomUUID().toString(), input, player1, player2, player3, player4)).block();

//        rabbitTemplate.convertAndSend(MatchRabbitConfig.MATCH_EXCHANGE, MatchRabbitConfig.MATCH_QUEUE, savedMatch);

        return mapper.toMatchResponse(savedMatch);
    }

    @NotNull
    private MatchPlayer getPlayerOrOnboardPlayer(String playerId, boolean isPrincipal) {
        return playerService.findById(playerId)
                .map((p) -> mapper.toMatchPlayer(p, isPrincipal ? MatchStatus.APPROVED : MatchStatus.PENDING))
                .orElseThrow();
    }
}
