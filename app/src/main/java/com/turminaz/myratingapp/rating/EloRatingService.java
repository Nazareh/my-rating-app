package com.turminaz.myratingapp.rating;

import com.turminaz.myratingapp.model.Match;
import com.turminaz.myratingapp.model.Player;
import com.turminaz.myratingapp.player.PlayerRepository;
import com.turminaz.myratingapp.model.Rating;
import lombok.extern.log4j.Log4j2;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.turminaz.myratingapp.utils.MatchUtils.getWinnerTeam;

@Service
@Log4j2
public class EloRatingService extends RatingService {

    private static final int INITIAL_RATING = 1500;
    private static final int K_FACTOR_NEW_PLAYER = 40;
    private static final int K_FACTOR_ESTABLISHED_PLAYER = 20;
    private static final int K_FACTOR_MATCHES_THRESHOLD = 10;


    public EloRatingService(PlayerRepository playerRepository, JmsTemplate jmsTemplate) {
        super(RatingType.ELO, jmsTemplate, playerRepository);
    }

    @Override
    @JmsListener(destination = "matchCreated")
    public void calculateRating(Match match) {
        log.info("Calculating {} rating", ratingType);

        match.getPlayers().forEach(p -> {
            var player = repository.findById(p.getId()).orElseThrow();

            var ratings = player.getRatings();

            var lastRatingValue = ratings.isEmpty()
                    ? INITIAL_RATING
                    : Integer.parseInt(ratings.get(ratingType.name()).getLast().getValue());

            var partnerRating = match.getPlayers().stream()
                    .filter(it -> it.getTeam().equals(p.getTeam()))
                    .filter(it -> !it.getId().equals(p.getId()))
                    .map(it -> it.getRatings() == null || !it.getRatings().containsKey(ratingType.name())
                            ? INITIAL_RATING
                            : Integer.parseInt(it.getRatings().get(ratingType.name()).getValue()))
                    .reduce(0, Integer::sum);

            var combinedRating = lastRatingValue + partnerRating;
            var opponentsRating = match.getPlayers().stream().filter(it -> it.getTeam() != p.getTeam())
                    .map(it ->
                            (it.getRatings() == null || it.getRatings().isEmpty())
                            ? INITIAL_RATING
                                    : Integer.parseInt(it.getRatings().get(ratingType.name()).getValue()))
                    .reduce(0, Integer::sum);

            var newRatingValue = calculateElo(combinedRating, opponentsRating, getWinnerTeam(match) == p.getTeam(),
                    calculateKFactor(player)) / 2 + lastRatingValue;

            if (ratings.isEmpty()) {
                ratings.put(this.ratingType.name(), new ArrayList<>());
            }

            ratings.get(this.ratingType.name()).add(new Rating(this.ratingType.name(), match.getId(), match.getStartTime(), String.valueOf(newRatingValue)));

            var lastRating = ratings.get(this.ratingType.name()).getLast();

            if (lastRating.getDateTime().isAfter(match.getStartTime())) {
                throw new IllegalArgumentException(String.format("A future rating already exists. LastRatingDate=%s MatchId=%s MatchDate=%s",
                        lastRating.getDateTime(), match.getId(), match.getStartTime()));
            }
            repository.save(player);
        });

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

    private int calculateKFactor(Player playerRating) {
        return playerRating.getMatchesWon() + playerRating.getMatchesLost() <= K_FACTOR_MATCHES_THRESHOLD
                ? K_FACTOR_NEW_PLAYER : K_FACTOR_ESTABLISHED_PLAYER;

    }
}
