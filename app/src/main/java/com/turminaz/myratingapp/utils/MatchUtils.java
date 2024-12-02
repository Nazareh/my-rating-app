package com.turminaz.myratingapp.utils;

import com.turminaz.myratingapp.match.Team;
import com.turminaz.myratingapp.model.Match;
import com.turminaz.myratingapp.model.SetScore;

import java.util.HashSet;
import java.util.Optional;

public class MatchUtils {
    static boolean isSetScoreValid(SetScore setScore) {
        return setScore.getTeam1() >= 0 &&
                setScore.getTeam2() >= 0 &&
                setScore.getTeam1() + setScore.getTeam2() > 0 &&
                setScore.getTeam1() !=  setScore.getTeam2();
    }
    public static Optional<Team> getWinnerTeam(Match match) {

         if(match.getScores() == null || match.getScores().isEmpty()) return Optional.empty();
         Team winner = null;

         for (SetScore score:match.getScores()) {
             var setWinner = getSetWinner(score);

             if (winner == null) {
                 winner = setWinner;
             }
             else if (winner != setWinner){
                 winner = null;
             };
         }

         return Optional.ofNullable(winner);

    }

    static boolean validateDistinctPlayers(Match match) {
        return new HashSet<>(match.getPlayers()).size() == 4;
    }

    static Team getSetWinner(SetScore setScore) {
        return setScore.getTeam1() > setScore.getTeam2()
               ? Team.TEAM_1
               :Team.TEAM_2;
    }

}
