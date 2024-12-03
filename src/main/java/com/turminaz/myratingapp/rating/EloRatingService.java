package com.turminaz.myratingapp.rating;

import com.turminaz.myratingapp.model.*;
import com.turminaz.myratingapp.player.PlayerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.turminaz.myratingapp.utils.MatchUtils.getWinnerTeam;

@Service
@Log4j2
@RequiredArgsConstructor
public class EloRatingService {

    private final RatingType ratingType = RatingType.ELO;
    private final PlayerRepository repository;

    private static final int INITIAL_RATING = 1500;
    private static final int K_FACTOR = 40;


//    @JmsListener(destination = "matchCreated")
    public void calculateRating(Match match) {
        log.info("Calculating {} rating", ratingType);

        match.getPlayers().forEach(matchPlayer -> {
            var player = repository.findById(matchPlayer.getId()).orElseThrow();

            var ratings = player.getRatings();

            var lastRatingValue = ratings.isEmpty()
                    ? INITIAL_RATING
                    : Integer.parseInt(ratings.get(ratingType.name()).getLast().getValue());

            var partnerRating = match.getPlayers().stream()
                    .filter(it -> it.getTeam().equals(matchPlayer.getTeam()))
                    .map(MatchPlayer::getId)
                    .filter(id -> !id.equals(matchPlayer.getId()))
                    .map(repository::findById)
                    .map(Optional::orElseThrow)
                    .map(p -> getLastRating(match, p))
                    .reduce(0, Integer::sum);

            var teamRatingAvg = Math.ceilDiv((lastRatingValue + partnerRating), 2);
            var opponentsRatingAvg = Math.ceilDiv(
                    match.getPlayers().stream()
                            .filter(it -> it.getTeam() != matchPlayer.getTeam())
                            .map(MatchPlayer::getId)
                            .map(repository::findById)
                            .map(Optional::orElseThrow)
                            .map(p -> getLastRating(match, p))
                            .reduce(0, Integer::sum), 2);

            var winRatio = calculateGameWinRatio(match);
            var newRatingValue = calculateElo(teamRatingAvg, opponentsRatingAvg, getWinnerTeam(match).orElseThrow() == matchPlayer.getTeam(),
                    calculateKFactor(winRatio)) + lastRatingValue;

            if (ratings.isEmpty()) {
                ratings.put(this.ratingType.name(), new ArrayList<>());
            }

            ratings.get(this.ratingType.name()).add(new Rating(this.ratingType.name(), match.getId(), match.getStartTime(), String.valueOf(newRatingValue)));
            if (matchPlayer.getId().equals("pawel")) {
                System.out.println("hello me");
            }
            repository.save(player);
        });

    }

    private float calculateGameWinRatio(Match match) {
        int team1Games = match.getScores().stream().map(SetScore::getTeam1).reduce(0,Integer::sum);
        int team2Games = match.getScores().stream().map(SetScore::getTeam2).reduce(0,Integer::sum);

        return 1 - (float) Math.min(team1Games, team2Games) / Math.max(team1Games, team2Games) ;

    }

    @NotNull
    private Integer getLastRating(Match match, Player p) {
        if (p.getRatings() == null || !p.getRatings().containsKey(ratingType.name()))
            return INITIAL_RATING;
        var filteredRatings = p.getRatings().get(ratingType.name()).stream()
                .filter(rating -> !rating.getMatchId().equals(match.getId())).toList();
        return filteredRatings.isEmpty()
                ? INITIAL_RATING
                : Integer.parseInt(filteredRatings.getLast().getValue());
    }

    private int calculateElo(int playerRating, int opponentRating, boolean hasPlayerWon, int kFactor) {
        var hasWon = hasPlayerWon ? 1 : 0;
        double team1ExpectedScore = calculateExpectedScore(playerRating, opponentRating);

        return calculateRatingIncrease(team1ExpectedScore, hasWon, kFactor);
    }

    private static double calculateExpectedScore(int playerRating, int opponentRating) {
        return 1.0 / (1 + Math.pow(10, (double) (opponentRating - playerRating) / 400));
    }

    private static int calculateRatingIncrease(double expectedScore, int result, int kFactor) {
        return (int) (kFactor * (result - expectedScore));
    }

    private int calculateKFactor(float winRation) {
        return Math.round(K_FACTOR * winRation);
    }
}
