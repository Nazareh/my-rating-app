package com.turminaz.myratingapp.utils;

import com.turminaz.myratingapp.match.Team;
import com.turminaz.myratingapp.model.Match;

public class MatchUtils {

    public static boolean isSetResultValid(int team1Result, int team2Result) {
        return (team1Result >= 0 && team2Result >= 0)
                && (team1Result + team2Result > 0);

    }

    public static boolean isMatchResultValid(Match match) {
        //todo write tests
        var isFirstSetValid = isSetResultValid(match.getSet1Team1Score(), match.getSet1Team2Score());
        var isSecondSetValid = isSetResultValid(match.getSet2Team1Score(), match.getSet2Team2Score());
        var isThirdSetValid = isSetResultValid(match.getSet3Team1Score(), match.getSet3Team2Score());

        if (!isFirstSetValid) return false;
        if (isSecondSetValid == isThirdSetValid) return true;

        return isSecondSetValid
                && ((match.getSet1Team1Score() > match.getSet1Team2Score() && match.getSet2Team1Score() > match.getSet2Team2Score())
                || (match.getSet1Team1Score() < match.getSet1Team2Score() && match.getSet2Team1Score() < match.getSet2Team2Score()));

    }

    public static Team getWinnerTeam(Match match) {
        var winnerSet1 = match.getSet1Team1Score() > match.getSet1Team2Score() ? Team.TEAM_1 : Team.TEAM_2;

        if (isSetResultValid(match.getSet2Team1Score(), match.getSet3Team1Score())) {
            var winnerSet2 = match.getSet2Team1Score() > match.getSet2Team2Score() ? Team.TEAM_1 : Team.TEAM_2;
            if (winnerSet1 == winnerSet2) return Team.TEAM_1;
        }

        return match.getSet1Team1Score() > match.getSet1Team2Score() ? Team.TEAM_1 : Team.TEAM_2;

    }
}
