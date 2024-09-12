package com.turminaz.myratingapp.rating;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class EloCalculator {


//    public static Map<String, Integer> calculateElo(PlayerRating[] team1Ratings, PlayerRating[] team2Ratings, int team1Won) {
//        if (team1Won < 0 || team1Won > 1) {
//            throw new IllegalArgumentException("team1Won argument must be 0, 0.5 or 1 to be able to determine if team lost, drew or won");
//        }
//
//        double team1ExpectedScore = calculateExpectedScore(team1Ratings, team2Ratings);
//        double team2ExpectedScore = 1 - team1ExpectedScore;
//
//        Map<String, Integer> newRatings = new HashMap<>();
//
//        for (int i = 0; i < 2; i++) {
//            var team1Player = team1Ratings[i];
//            var team2Player = team2Ratings[i];
//
//            // Calculate new rating for each player in team 1
//            int team1NewRating = calculateNewRating(team1Player.getCurrentElo(), team1ExpectedScore, team1Won, team1Player.getCurrentKFactor());
//            // Calculate new rating for each player in team 2
//            int team2NewRating = calculateNewRating(team2Player.getCurrentElo(), team2ExpectedScore, team1Won == 1 ? 0 : team1Won, team2Player.getCurrentKFactor());
//
//            newRatings.put(team1Player.getId(),team1NewRating);
//            newRatings.put(team2Player.getId(),team2NewRating);
//
//        }
//
//        return newRatings;
//    }
//
//    private static double calculateExpectedScore(PlayerRating[] team1Ratings, PlayerRating[] team2Ratings) {
//        double team1AverageRating = (team1Ratings[0].getCurrentElo() + team1Ratings[1].getCurrentElo()) / 2.0;
//        double team2AverageRating = (team2Ratings[0].getCurrentElo() + team2Ratings[1].getCurrentElo()) / 2.0;
//
//        return 1.0 / (1 + Math.pow(10, (team2AverageRating - team1AverageRating) / 400));
//    }
//
//
//    // Function to calculate new rating based on outcome
//    private static int calculateNewRating(int oldRating, double expectedScore, int result, int kFactor) {
//        return (int) (oldRating + kFactor * (result - expectedScore));
//    }
}
