package com.turminaz.myratingapp.rating;

import com.turminaz.myratingapp.match.Team;
import com.turminaz.myratingapp.model.Match;
import com.turminaz.myratingapp.model.MatchPlayer;
import com.turminaz.myratingapp.model.Player;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;

import static com.turminaz.myratingapp.utils.MatchUtils.getWinnerTeam;


@Log4j2
@RequiredArgsConstructor
public abstract class RatingService {

    public final RatingType ratingType;
    public final JmsTemplate jmsTemplate;

    protected final PlayerRatingRepository repository;

    protected abstract void calculateRating(Match match);

    @JmsListener(destination = "playerCreated")
    private void receiveMatchCreated(Player player) {
        log.info("Received {}", player);
        repository.save(new PlayerRating().setId(player.getId())).block();
    }

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

}
