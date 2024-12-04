package com.turminaz.myratingapp.match;

import com.turminaz.myratingapp.config.AuthenticationFacade;
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
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;
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

    public void republishedApprovedMatches() {
        repository.findAllByStatus(MatchStatus.APPROVED)
                .forEach(this::processApprovedMatch);
    }

    MatchDto postMatch(PostMatchDto matchDto) {
        return processMatches(Stream.of(mapper.toMatch(matchDto))).getFirst();
    }

    List<MatchDto> getAllMatches() {
        return repository.findAll().stream()
                .map(mapper::toMatchDto)
                .collect(Collectors.toList());
    }

    List<MatchDto> uploadMatchFromCsv(InputStream inputStream) {
        return processMatches(mapper.toMatchStream(inputStream));
    }

    private List<MatchDto> processMatches(Stream<Match> matchStream) {
        var authenticatedUserUid = authenticationFacade.authenticatedUser();
        var isAdmin = authenticatedUserUid.getCustomClaims().get("admin").equals(true);

        return matchStream
                .sorted(Comparator.comparing(Match::getStartTime))
                .peek(match -> match.getPlayers()
                        .forEach(matchPlayer -> updateMatchPlayerDetails(
                                matchPlayer, authenticatedUserUid.getUid(),
                                () -> playerService.isValidEmail(matchPlayer.getId())
                                        ? playerService.findByEmailOrCreate(matchPlayer.getId())
                                        : playerService.findById(matchPlayer.getId())
                                )))
                .peek(match -> updateMatchStatus(match, isAdmin))
                .map(repository::save)
                .peek(match -> {
                    if (match.getStatus() == MatchStatus.APPROVED) {
                        processApprovedMatch(match);
                    }
                })
                .map(mapper::toMatchDto)
                .toList();
    }

    private void processApprovedMatch(Match match){
        match.getPlayers().forEach(p -> playerService.updatePlayerStats(p, match));
        eloRatingService.calculateRating(match);
    }

    private void updateMatchPlayerDetails(MatchPlayer matchPlayer, String authenticatedUserUid, Supplier<Player> playerSupplier) {
        var player = playerSupplier.get();
        matchPlayer.setId(player.getId());
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

        if(match.getStartTime().isAfter(Instant.now())) {
            match.setStatus(MatchStatus.INVALID);
            match.setReason("Future matches are not allowed");
        }

        if (players.size() != 4) {
            match.setStatus(MatchStatus.INVALID);
            match.setReason("Four distinct players are needed");
        }

        if (getWinnerTeam(match).isEmpty()){
            match.setStatus(MatchStatus.INVALID);
            match.setReason("It was not possible determine a winner team");
        }

        var hasNoFutureMatches = repository.findAllByStartTimeGreaterThan(match.getStartTime().minus(1, ChronoUnit.MINUTES))
                .stream()
                .noneMatch(m -> m.getPlayers().stream().map(MatchPlayer::getId).anyMatch(players::contains));

        if(!hasNoFutureMatches){
            match.setStatus(MatchStatus.INVALID);
            match.setReason("Another match already exists for the same time or future");
        }

        if(!hasNoFutureMatches){
            match.setStatus(MatchStatus.INVALID);
            match.setReason("Another match already exists for the same time or future");
        }

        if (match.getStatus().equals(MatchStatus.PENDING) && postedByAdmin){
            match.setStatus(MatchStatus.APPROVED);
            match.setReason("Match approved automatically");
        }
    }
}
