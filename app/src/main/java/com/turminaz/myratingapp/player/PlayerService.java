package com.turminaz.myratingapp.player;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.netflix.dgs.codegen.generated.types.PlayerResponse;
import com.opencsv.bean.CsvToBeanBuilder;
import com.turminaz.myratingapp.match.Team;
import com.turminaz.myratingapp.model.Match;
import com.turminaz.myratingapp.model.MatchPlayer;
import com.turminaz.myratingapp.model.MatchStatus;
import com.turminaz.myratingapp.model.Player;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.turminaz.myratingapp.utils.MatchUtils.getWinnerTeam;

@Service
@RequiredArgsConstructor
@Log4j2
public class PlayerService {
    private final PlayerRepository repository;
    private final PlayerMapper mapper;
    private final FirebaseAuth firebaseAuth;
    private final JmsTemplate jmsTemplate;

    public Optional<Player> findById(String id) {
        return repository.findById(id).blockOptional();
    }

    public Player createPlayer(String id) {
        try {
            return repository.save(mapper.toPlayer(firebaseAuth.getUser(id))).block();
        } catch (FirebaseAuthException e) {
            throw new RuntimeException(e);
        }
    }

    PlayerResponse onboardPlayer(String id) {
        return mapper.toPlayerResponse(createPlayer(id));
    }

    Set<PlayerDto> registerPlayersFromCsv(InputStream inputStream) {

        return new CsvToBeanBuilder<RegisterPlayerDto>(new InputStreamReader(inputStream))
                .withType(RegisterPlayerDto.class)
                .build().parse().stream()
                .map(mapper::toPlayer)
                .map(p -> repository.findByEmail(p.getEmail()).blockOptional()
                        .orElseGet(() -> repository.save(p).block()))
                .filter(Objects::nonNull)
                .map(mapper::toPlayerDto)
                .collect(Collectors.toSet());
    }

    List<PlayerDto> getAllPlayers() {
        return repository.findAll().collectList().block().stream().map(mapper::toPlayerDto).collect(Collectors.toList());
    }

    @JmsListener(destination = "matchCreated")
    private void receiveMatchCreated(Match match) {
        log.info("Received match {}", match.getId());
        match.getPlayers().stream()
                .map(matchPlayer -> updatePlayerStats(matchPlayer, match))
                .forEach(player -> repository.save(player).block());
    }

    private Player updatePlayerStats(MatchPlayer matchPlayer, Match match) {
        var player = repository.findById(matchPlayer.getId()).blockOptional().orElseThrow();
        if (getWinnerTeam(match) == matchPlayer.getTeam())
            player.setMatchesWon(player.getMatchesWon() + 1);
        else
            player.setMatchesLost(player.getMatchesLost() + 1);

        var team1GamesWon = match.getSet1Team1Score() + match.getSet2Team1Score() + match.getSet3Team1Score();
        var team2GamesWon = match.getSet1Team2Score() + match.getSet2Team2Score() + match.getSet3Team2Score();

        if (matchPlayer.getTeam() == Team.TEAM_1) {
            player.setGamesWon(player.getGamesWon() + team1GamesWon);
            player.setGamesLost(player.getGamesLost() + team2GamesWon);
        } else {
            player.setGamesWon(player.getGamesWon() + team2GamesWon);
            player.setGamesLost(player.getGamesLost() + team1GamesWon);
        }

        return player;
    }
}
