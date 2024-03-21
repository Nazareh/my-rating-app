package com.turminaz.myratingapp.match;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.netflix.dgs.codegen.generated.types.MatchInput;
import com.netflix.dgs.codegen.generated.types.MatchResponse;
import com.turminaz.myratingapp.model.Player;
import com.turminaz.myratingapp.player.PlayerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
class MatchService {

    private final MatchMapper mapper;
    private final RabbitTemplate rabbitTemplate;

    private final PlayerService playerService;

    MatchResponse createMatch(MatchInput input, Principal principal) {

        log.info("Creating match: {}", input);

        var player1 = getPlayerOrOnboardPlayer(input.getTeam1().getMatchPlayer1(),
                principal.getName().equals(input.getTeam1().getMatchPlayer1()));
        var player2 = getPlayerOrOnboardPlayer(input.getTeam1().getMatchPlayer2(),
                principal.getName().equals(input.getTeam1().getMatchPlayer2()));
        var player3 = getPlayerOrOnboardPlayer(input.getTeam2().getMatchPlayer1(),
                principal.getName().equals(input.getTeam2().getMatchPlayer1()));
        var player4 = getPlayerOrOnboardPlayer(input.getTeam2().getMatchPlayer2(),
                principal.getName().equals(input.getTeam2().getMatchPlayer2()));

        var savedMatch = mapper.toMatch(UUID.randomUUID().toString(), input, player1, player2, player3, player4);

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
