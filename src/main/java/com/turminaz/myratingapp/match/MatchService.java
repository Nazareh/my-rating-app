package com.turminaz.myratingapp.match;

import com.turminaz.myratingapp.playerMatchService.PlayerMatchService;
import com.turminaz.myratingapp.config.AuthenticationFacade;
import com.turminaz.myratingapp.dto.MatchDto;
import com.turminaz.myratingapp.model.Match;
import com.turminaz.myratingapp.model.MatchPlayer;
import com.turminaz.myratingapp.model.MatchStatus;
import com.turminaz.myratingapp.model.Player;
import com.turminaz.myratingapp.player.PlayerService;
import com.turminaz.myratingapp.rating.EloRatingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.turminaz.myratingapp.utils.MatchUtils.getWinnerTeam;

@Service
@Slf4j
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository repository;
    private final PlayerService playerService;
    private final EloRatingService eloRatingService;
    private final AuthenticationFacade authenticationFacade;
    private final MatchMapper mapper;
    private final PlayerMatchService playerMatchService;

    public void republishedApprovedMatches() {
        repository.findAllByStatus(MatchStatus.APPROVED)
                .forEach(this::processApprovedMatch);
    }

    MatchDto postMatch(PostMatchDto matchDto) {
        return createMatches(Stream.of(mapper.toMatch(matchDto))).getFirst();
    }

    List<MatchDto> getMatches(Optional<MatchStatus> status)  {

        var userUid = authenticationFacade.getUserUid();
        var playerId = playerService.findByUserUidOrOnboard(userUid).orElseThrow().getId();

        return playerMatchService.getMatchesByPlayer(playerId, status);
    }

    List<MatchDto> uploadMatchFromCsv(InputStream inputStream) {
        return createMatches(mapper.toMatchStream(inputStream));
    }

    MatchDto approve(String matchId) {
        return updateMatchStatus(matchId, MatchStatus.APPROVED);
    }

    MatchDto reject(String matchId) {
        return updateMatchStatus(matchId, MatchStatus.REJECTED);
    }

    private MatchDto updateMatchStatus(String matchId, MatchStatus newStatus) {
        var match = repository.findById(matchId).orElseThrow();
        var players = match.getPlayers();
        var isAdmin = authenticationFacade.isAdmin();
        var playerId = playerService.findByUserUidOrOnboard(authenticationFacade.getUserUid()).orElseThrow().getId().toString();

        if (!isAdmin && players.stream().map(MatchPlayer::getId).noneMatch(id -> id.equals(playerId))) {
            throw new RuntimeException("Player did not played was not part of the given game");
        }

        players.stream()
                .filter(matchPlayer -> matchPlayer.getId().equals(playerId))
                .filter(matchPlayer -> matchPlayer.getStatus().equals(MatchStatus.PENDING))
                .forEach(matchPlayer -> matchPlayer.setStatus(newStatus)
        );

        if (match.getStatus().equals(MatchStatus.PENDING) &&
                players.stream().map(MatchPlayer::getStatus).allMatch(status -> status.equals(MatchStatus.APPROVED))) {
            match.setStatus(MatchStatus.APPROVED);
        } else if (players.stream().map(MatchPlayer::getStatus).anyMatch(status -> status.equals(MatchStatus.REJECTED))) {
            match.setStatus(MatchStatus.REJECTED);
        }

        if (isAdmin){
            match.setStatus(newStatus);
        }

        if(match.getStatus().equals(MatchStatus.APPROVED)){
            processApprovedMatch(match);
        }

        return mapper.toMatchDto(repository.save(match));

    }

    private List<MatchDto> createMatches(Stream<Match> matchStream) {
        var isAdmin = authenticationFacade.isAdmin();
        Set<Player> players = new HashSet<>();

        return matchStream
                .sorted(Comparator.comparing(Match::getStartTime))
                .peek(match -> match.getPlayers()
                        .forEach(matchPlayer -> {
                            var player = playerService.isValidEmail(matchPlayer.getId())
                                    ? playerService.findByEmailOrCreate(matchPlayer.getId())
                                    : playerService.findById(matchPlayer.getId());

                             updateMatchPlayerDetails(matchPlayer, authenticationFacade.getUserUid(),player);
                            players.add(player);
                        }))
                .peek(match -> updateMatchStatus(match, isAdmin))
                .map(repository::save)
                .peek(match -> {
                    if (match.getStatus() == MatchStatus.APPROVED)
                        processApprovedMatch(match);
                })
                .map(mapper::toMatchDto)
                .toList();
    }

    private void processApprovedMatch(Match match) {
        match.getPlayers().forEach(p -> playerService.updatePlayerStats(p, match));
        eloRatingService.calculateRating(match);
    }

    private void updateMatchPlayerDetails(MatchPlayer matchPlayer, String authenticatedUserUid, Player player) {
        matchPlayer.setId(player.getId().toString());
        matchPlayer.setName(player.getName());
        matchPlayer.setStatus(
                player.getUserUid() != null && player.getUserUid().equals(authenticatedUserUid)
                        ? MatchStatus.APPROVED
                        : MatchStatus.PENDING);
    }

    private void updateMatchStatus(Match match, boolean postedByAdmin) {
        var players = match.getPlayers().stream().map(MatchPlayer::getId).collect(Collectors.toSet());

        match.setStatus(MatchStatus.PENDING);
        match.setReason("Pending approval of all players");

        if (match.getStartTime().isAfter(Instant.now())) {
            match.setStatus(MatchStatus.INVALID);
            match.setReason("Future matches are not allowed");
        }

        if (players.size() != 4) {
            match.setStatus(MatchStatus.INVALID);
            match.setReason("Four distinct players are needed");
        }

        if (getWinnerTeam(match).isEmpty()) {
            match.setStatus(MatchStatus.INVALID);
            match.setReason("It was not possible determine a winner team");
        }

        var hasNoFutureMatches = repository.findAllByStartTimeGreaterThan(match.getStartTime().minus(1, ChronoUnit.MINUTES))
                .stream()
                .noneMatch(m -> m.getPlayers().stream().map(MatchPlayer::getId).anyMatch(players::contains));

        if (!hasNoFutureMatches) {
            match.setStatus(MatchStatus.INVALID);
            match.setReason("Another match already exists for the same time or future");
        }

        if (!hasNoFutureMatches) {
            match.setStatus(MatchStatus.INVALID);
            match.setReason("Another match already exists for the same time or future");
        }

        if (match.getStatus().equals(MatchStatus.PENDING) && postedByAdmin) {
            match.setStatus(MatchStatus.APPROVED);
            match.setReason("Match approved automatically");
        }
    }
}
