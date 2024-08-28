package com.turminaz.myratingapp.rating;

import com.google.common.annotations.VisibleForTesting;
import com.netflix.dgs.codegen.generated.types.MatchResponse;
import com.netflix.dgs.codegen.generated.types.SetResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final PlayerRatingRepository playerRatingRepository;

    private static final int INITIAL_RATING = 1500;
    private static final int K_FACTOR_NEW_PLAYER = 40;
    private static final int K_FACTOR_ESTABLISHED_PLAYER = 20;
    private static final int K_FACTOR_MATCHES_THRESHOLD = 10;

    void calculateElo(MatchResponse match) {
        var player1 = getPlayerOrCreateNew(match.getTeam1().getMatchPlayer1().getId());
        var player2 = getPlayerOrCreateNew(match.getTeam1().getMatchPlayer2().getId());
        var player3 = getPlayerOrCreateNew(match.getTeam2().getMatchPlayer1().getId());
        var player4 = getPlayerOrCreateNew(match.getTeam2().getMatchPlayer2().getId());

        player1.setCurrentKFactor(calculateKFactor(player1));
        player2.setCurrentKFactor(calculateKFactor(player2));
        player3.setCurrentKFactor(calculateKFactor(player3));
        player4.setCurrentKFactor(calculateKFactor(player4));

        PlayerRating[] team1Ratings = {player1, player2};
        PlayerRating[] team2Ratings = {player3, player4};

//        var newElos = EloCalculator.calculateElo(team1Ratings, team2Ratings, match.get)


    }

    @VisibleForTesting
    boolean hasTeam1Won(List<SetResponse> sets){
        var team1WonSets = sets.stream().filter(set -> set.getTeam1Score() > set.getTeam2Score()).count();
        if (team1WonSets == sets.size() / 2){
            throw new RuntimeException("It was not possible to determine");
        }
        return team1WonSets > (int) Math.ceil((double) sets.size() / 2);
    }

    private PlayerRating getPlayerOrCreateNew(String playerId) {
        var player = playerRatingRepository.findById(playerId).blockOptional();

        return player.orElseGet(() -> new PlayerRating(
                playerId,
                INITIAL_RATING,
                K_FACTOR_NEW_PLAYER, 0, 0, 0, 0, 0, 0, 0, 0, null, null
        ));

    }

    private int calculateKFactor(PlayerRating playerRating) {
        return playerRating.getTotalGames() <= K_FACTOR_MATCHES_THRESHOLD
                ? K_FACTOR_NEW_PLAYER : K_FACTOR_ESTABLISHED_PLAYER;

    }
}
