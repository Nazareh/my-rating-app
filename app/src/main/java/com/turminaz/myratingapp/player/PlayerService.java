package com.turminaz.myratingapp.player;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.netflix.dgs.codegen.generated.types.PlayerResponse;
import com.opencsv.bean.CsvToBeanBuilder;
import com.turminaz.myratingapp.Topics;
import com.turminaz.myratingapp.match.Team;
import com.turminaz.myratingapp.model.Match;
import com.turminaz.myratingapp.model.MatchPlayer;
import com.turminaz.myratingapp.model.Player;
import com.turminaz.myratingapp.model.SetScore;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

import static com.turminaz.myratingapp.utils.MatchUtils.getWinnerTeam;

@Service
@RequiredArgsConstructor
@Log4j2
public class PlayerService {
    private final PlayerRepository repository;
    private final FirebaseAuth firebaseAuth;
    private final PlayerMapper mapper;

    public final Player findById(String id) {
        return repository.findById(id).orElseThrow();
    }

    public final Player findByEmailOrCreate(String email) {
        return repository.findByEmail(email)
                .or(() -> Optional.of(
                        repository
                                .save(new Player().setEmail(email)
                                        .setName(email.contains("@") ? email.split("@")[0] : email))))
                .get();
    }

    public void eraseAllRatings() {
        repository.findAll().forEach(
                player ->
                        repository.save(player.setGamesLost(0).setGamesWon(0).setMatchesWon(0).setMatchesLost(0).setRatings(new HashMap<>())));
    }

    PlayerResponse onboardPlayer(String userUid) throws FirebaseAuthException {
        var userRecord = firebaseAuth.getUser(userUid);
        var existingPlayer = repository.findByEmail(userRecord.getEmail());
        var savedPlayer = repository.save(existingPlayer.isPresent()
                ? existingPlayer.get().setUserUid(userUid).setName(userRecord.getDisplayName())
                : mapper.toPlayer(userRecord)
        );
        return mapper.toPlayerResponse(savedPlayer);
    }

    Set<PlayerDto> registerPlayersFromCsv(InputStream inputStream) {
        return new CsvToBeanBuilder<RegisterPlayerDto>(new InputStreamReader(inputStream))
                .withType(RegisterPlayerDto.class)
                .build().parse().stream()
                .map(mapper::toPlayer)
                .map(repository::save)
                .map(mapper::toPlayerDto)
                .collect(Collectors.toSet());
    }

    List<PlayerDto> getAllPlayers() {
        return repository.findAll().stream().map(mapper::toPlayerDto).collect(Collectors.toList());
    }

    PlayerDto getPlayerById(String id) {
        return repository.findById(id)
                .map(mapper::toPlayerDto)
                .orElseThrow();
    }

//    @JmsListener(destination = Topics.MATCH_CREATED)
//    private void receiveMatchCreated(Match match) {
//        match.getPlayers()
//                .forEach(matchPlayer -> updatePlayerStats(matchPlayer, match));
//    }

    public void updatePlayerStats(MatchPlayer matchPlayer, Match match) {

        var player = repository.findById(matchPlayer.getId()).orElseThrow();

        if (getWinnerTeam(match).orElseThrow() == matchPlayer.getTeam())
            player.setMatchesWon(player.getMatchesWon() + 1);
        else
            player.setMatchesLost(player.getMatchesLost() + 1);

        int team1GamesWon = match.getScores().stream().map(SetScore::getTeam1).reduce(0,Integer::sum);
        int team2GamesWon = match.getScores().stream().map(SetScore::getTeam2).reduce(0,Integer::sum);

        if (matchPlayer.getTeam() == Team.TEAM_1) {
            player.setGamesWon(player.getGamesWon() + team1GamesWon);
            player.setGamesLost(player.getGamesLost() + team2GamesWon);
        } else {
            player.setGamesWon(player.getGamesWon() + team2GamesWon);
            player.setGamesLost(player.getGamesLost() + team1GamesWon);
        }

       var savedPlayer =  repository.save(player);

        if (player.getName().contains("Eric")) {
            System.out.println(savedPlayer);
        }

    }
}
