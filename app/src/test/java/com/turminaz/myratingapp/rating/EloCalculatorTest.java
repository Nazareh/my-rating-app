package com.turminaz.myratingapp.rating;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class EloCalculatorTest {

    PlayerRating[] team1Ratings = new PlayerRating[2];
    PlayerRating[] team2Ratings = new PlayerRating[2];

    private static Stream<Arguments> provideExpectedNewRatings() {
        return Stream.of(
                Arguments.of(1, new int[]{1600, 1550, 1500, 1450}, new int[]{1611, 1561, 1488, 1438}, new int[]{32, 32, 32, 32}),
                Arguments.of(-1, new int[]{1600, 1550, 1500, 1450}, new int[]{1579, 1529, 1520, 1470}, new int[]{32, 32, 32, 32}),
                Arguments.of(-1, new int[]{1500, 1500, 1500, 1500}, new int[]{1484, 1484, 1516, 1516}, new int[]{32, 32, 32, 32}),
                Arguments.of(-1, new int[]{1500, 2000, 1700, 1800}, new int[]{1484, 1984, 1716, 1816}, new int[]{32, 32, 32, 32}),
                Arguments.of(-1, new int[]{800, 800, 1700, 1800}, new int[]{799, 799, 1700, 1800}, new int[]{32, 32, 32, 32}),
                Arguments.of(1, new int[]{800, 900, 1700, 1800}, new int[]{831, 931, 1668, 1768}, new int[]{32, 32, 32, 32}),
                Arguments.of(0, new int[]{800, 900, 1700, 1800}, new int[]{831, 931, 1668, 1768}, new int[]{32, 32, 32, 32})

        );
    }

    @ParameterizedTest
    @MethodSource("provideExpectedNewRatings")
    void testCalculateEloTeam1Wins(int team1Won, int[] initialRating, int[] expectedNewRatings, int[] kFactors) {

        team1Ratings[0] = new PlayerRating().setCurrentElo(initialRating[0]).setId("player1").setCurrentKFactor(kFactors[0]);
        team1Ratings[1] = new PlayerRating().setCurrentElo(initialRating[1]).setId("player2").setCurrentKFactor(kFactors[1]);
        team2Ratings[0] = new PlayerRating().setCurrentElo(initialRating[2]).setId("player3").setCurrentKFactor(kFactors[2]);
        team2Ratings[1] = new PlayerRating().setCurrentElo(initialRating[3]).setId("player4").setCurrentKFactor(kFactors[3]);

        var newRatings = EloCalculator.calculateElo(team1Ratings, team2Ratings, team1Won);

        assertThat(newRatings.get(team1Ratings[0].getId())).isEqualTo(expectedNewRatings[0]);
        assertThat(newRatings.get(team1Ratings[1].getId())).isEqualTo(expectedNewRatings[1]);
        assertThat(newRatings.get(team2Ratings[0].getId())).isEqualTo(expectedNewRatings[2]);
        assertThat(newRatings.get(team2Ratings[1].getId())).isEqualTo(expectedNewRatings[3]);

    }

}