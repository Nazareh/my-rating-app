package com.turminaz.myratingapp.rating;

import com.turminaz.myratingapp.model.*;
import com.turminaz.myratingapp.player.PlayerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.turminaz.myratingapp.utils.MatchUtils.getWinnerTeam;

@Service
@Log4j2
@RequiredArgsConstructor
public class EloRatingService {

    private final RatingType ratingType = RatingType.ELO;
    private final PlayerRepository repository;

    private static final int INITIAL_RATING = 1500;
    private static final int K_FACTOR = 40;

    public void calculateRating(Match match) {
        log.info("Calculating {} rating for matchId={}", ratingType, match.getId());

        Map<ObjectId, Player> playerMap =
                repository.findAllById(match.getPlayers().stream().map(MatchPlayer::getId)
                                .map(ObjectId::new)
                                .collect(Collectors.toList()))
                        .stream().collect(Collectors.toMap(Player::getId, player -> player));

        match.getPlayers().forEach(matchPlayer -> {
            log.info("Calculating {} rating for player id={} name={}", ratingType, matchPlayer.getId(), matchPlayer.getName());

            var player = playerMap.get(new ObjectId(matchPlayer.getId()));

            var ratings = player.getRatings();

            var lastRatingValue = ratings.isEmpty()
                    ? INITIAL_RATING
                    : ratings.get(ratingType).getLast().getValue();

            var partnerRating = match.getPlayers().stream()
                    .filter(it -> it.getTeam().equals(matchPlayer.getTeam()))
                    .map(MatchPlayer::getId)
                    .filter(id -> !id.equals(matchPlayer.getId()))
                    .map(ObjectId::new)
                    .map(repository::findById)
                    .map(Optional::orElseThrow)
                    .map(p -> getLastRating(match, p))
                    .reduce(0, Integer::sum);

            var teamRatingAvg = Math.ceilDiv((lastRatingValue + partnerRating), 2);
            var opponentsRatingAvg = Math.ceilDiv(
                    match.getPlayers().stream()
                            .filter(it -> it.getTeam() != matchPlayer.getTeam())
                            .map(MatchPlayer::getId)
                            .map(ObjectId::new)
                            .map(repository::findById)
                            .map(Optional::orElseThrow)
                            .map(p -> getLastRating(match, p))
                            .reduce(0, Integer::sum), 2);

            var winRatio = calculateGameWinRatio(match);
            var newRatingValue = calculateElo(teamRatingAvg, opponentsRatingAvg, getWinnerTeam(match).orElseThrow() == matchPlayer.getTeam(),
                    calculateKFactor(winRatio)) + lastRatingValue;

            if (ratings.isEmpty()) {
                ratings.put(this.ratingType, new ArrayList<>());
            }
            var newRating = new Rating(this.ratingType.name(), match.getId(), match.getStartTime(), newRatingValue);
            ratings.get(this.ratingType).add(newRating);
            player.getLastRatings().put(this.ratingType, newRating);

        });

        repository.saveAll(playerMap.values());

    }

    private float calculateGameWinRatio(Match match) {
        int team1Games = match.getScores().stream().map(SetScore::getTeam1).reduce(0, Integer::sum);
        int team2Games = match.getScores().stream().map(SetScore::getTeam2).reduce(0, Integer::sum);

        return 1 - (float) Math.min(team1Games, team2Games) / Math.max(team1Games, team2Games);

    }

    private Integer getLastRating(Match match, Player p) {

        return p.getRatings() != null && p.getLastRatings().containsKey(ratingType)
                ? p.getLastRatings().get(ratingType).getValue()
                : INITIAL_RATING;
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
