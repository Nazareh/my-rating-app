package com.turminaz.myratingapp.rating;

import com.google.common.annotations.VisibleForTesting;
import com.netflix.dgs.codegen.generated.types.MatchResponse;
import com.netflix.dgs.codegen.generated.types.SetResponse;
import com.turminaz.myratingapp.match.Team;
import com.turminaz.myratingapp.model.Match;
import com.turminaz.myratingapp.model.MatchPlayer;
import com.turminaz.myratingapp.model.Player;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.turminaz.myratingapp.utils.MatchUtils.getWinnerTeam;

@Service
@RequiredArgsConstructor
@Log4j2
public class RatingService {

    private final PlayerRatingRepository repository;

    private static final int INITIAL_RATING = 1500;
    private static final int K_FACTOR_NEW_PLAYER = 40;
    private static final int K_FACTOR_ESTABLISHED_PLAYER = 20;
    private static final int K_FACTOR_MATCHES_THRESHOLD = 10;



    @JmsListener(destination = "matchCreated")
    private void receiveMatchCreated(Match match) {
        log.info("Received match {}", match.getId());
        match.getPlayers().stream()
                .map(matchPlayer -> updatePlayerStats(matchPlayer, match))
                .forEach(player -> repository.save(player).block());
    }

    private PlayerRating updatePlayerStats(MatchPlayer matchPlayer, Match match) {
        var player = repository.findById(matchPlayer.getId()).blockOptional()
                .orElseGet(() -> repository.save(new PlayerRating()
                        .setId(matchPlayer.getId())).blockOptional().orElseThrow());

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

    private int calculateKFactor(PlayerRating playerRating) {
        return playerRating.getMatchesWon() + playerRating.getMatchesLost()  <= K_FACTOR_MATCHES_THRESHOLD
                ? K_FACTOR_NEW_PLAYER : K_FACTOR_ESTABLISHED_PLAYER;

    }

}
