package com.turminaz.myratingapp.match;

import java.util.List;
import java.util.Set;

public class MatchUtils {

    static boolean areAllPlayersOnTeam(Team team, Set<String> playersId) {
        return playersId.stream().allMatch(playerId -> isPlayerInTeam(team, playerId));
    }

    static boolean isAnyPlayerOnTeam(Team team, Set<String> playersId) {
        return playersId.stream().anyMatch(playerId -> isPlayerInTeam(team, playerId));
    }

    static boolean isPlayerInTeam(Team team, String playerId) {
        return getPlayersFromTeam(team).contains(playerId);
    }

    private static List<String> getPlayersFromTeam(Team team) {
        return List.of(team.getMatchPlayer1().getId(), team.getMatchPlayer2().getId());
    }

}
