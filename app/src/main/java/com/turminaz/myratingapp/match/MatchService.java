package com.turminaz.myratingapp.match;

import com.netflix.dgs.codegen.generated.types.MatchInput;
import com.netflix.dgs.codegen.generated.types.MatchResponse;
import com.opencsv.bean.CsvToBeanBuilder;
import com.turminaz.myratingapp.config.AuthenticationFacade;
import com.turminaz.myratingapp.model.Player;
import com.turminaz.myratingapp.player.PlayerService;
import com.turminaz.myratingapp.player.RegisterPlayerDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
//
//import static com.turminaz.myratingapp.match.MatchUtils.areAllPlayersOnTeam;
//import static com.turminaz.myratingapp.match.MatchUtils.isAnyPlayerOnTeam;

@Service
@Slf4j
@RequiredArgsConstructor
class MatchService {

    private final MatchRepository repository;
    private final PlayerService playerService;
    private final AuthenticationFacade authenticationFacade;
    private final MatchMapper mapper;

//    MatchResponse createMatch(MatchInput input) {
//        log.info("Creating match: {}", input);
//
//        validateFourDistinctPlayers(input);
//
//        var matches = repository.findAllByStartTime(input.getStartTime().toInstant()).collectList().block();
//        assert matches != null;
//
//        var existingMatch = findMatchWithAllPlayers(input, matches);
//        Match postedMatch = existingMatch.orElse(buildMatchObject(input));
//
//        validateNoPlayerIsOnTwoMatches(input, matches, postedMatch);
//
//        return approveMatchForPlayerAndSave(postedMatch);
//
//    }
//
//    MatchResponse approveMatch(String matchId) {
//        var match = repository.findById(matchId).block();
//        if (match == null) throw new RuntimeException("Match not found");
//
//        return approveMatchForPlayerAndSave(match);
//    }
//
//    MatchResponse rejectMatch(String matchId) {
//        var match = repository.findById(matchId).block();
//        if (match == null) throw new RuntimeException("Match not found");
//
//        log.info("Rejecting match {} ", match);
//
//        updatePlayerMatchStatus(match, MatchStatus.REJECTED);
//        match.setStatus(MatchStatus.REJECTED);
//
//        var savedMatch = repository.save(match).block();
//        return mapper.toMatchResponse(savedMatch);
//
//    }

    Set<MatchDto> uploadMatchFromCsv(InputStream inputStream) {
        return new CsvToBeanBuilder<PostMatchDto>(new InputStreamReader(inputStream))
                .withType(PostMatchDto.class)
                .build().parse().stream()
                .map(mapper::toMatch)
                .peek(match -> {
                    var allByStartTime = repository.findAllByStartTime(match.getStartTime()).collect(Collectors.toList()).block();
                    var players = match.getPlayers().stream().map(MatchPlayer::getId).toList();
                    assert allByStartTime != null;
                    var validMatch =  allByStartTime.stream().noneMatch(m -> m.getPlayers().stream().map(MatchPlayer::getId).anyMatch(players::contains));
                    match.setStatus(validMatch ? MatchStatus.APPROVED : MatchStatus.REJECTED);
                    match.setRejectedReason(validMatch ? null : "Another match already exists for the same time or future");
                })
                .peek(match -> match.getPlayers().forEach(matchPlayer -> matchPlayer.setName(playerService.findById(matchPlayer.getId()).orElseThrow().getName())))
                .map(match -> repository.save(match).block())
                .map(mapper::toMatchDto)
                .collect(Collectors.toSet());
    }

    List<MatchDto> getAllMatches() {
        return repository.findAll().collectList().block().stream()
                .map(mapper::toMatchDto)
                .collect(Collectors.toList());
    }
//
//    private MatchResponse approveMatchForPlayerAndSave(Match match) {
//        log.info("Approving match {}", match);
//
//        updatePlayerMatchStatus(match, MatchStatus.APPROVED);
//        approveMatchIfAllPlayersApproved(match);
//
//        var savedMatch = repository.save(match).block();
//        return mapper.toMatchResponse(savedMatch);
//    }
//
//    private void updatePlayerMatchStatus(Match postedMatch, MatchStatus matchStatus) {
//        var playerId = authenticationFacade.authenticatedUserId();
//
//        if (postedMatch.getTeam1().getMatchPlayer1().getId().equals(playerId)) {
//            postedMatch.getTeam1().getMatchPlayer1().setStatus(matchStatus);
//            return;
//        }
//        if (postedMatch.getTeam1().getMatchPlayer2().getId().equals(playerId)) {
//            postedMatch.getTeam1().getMatchPlayer2().setStatus(matchStatus);
//            return;
//        }
//        if (postedMatch.getTeam2().getMatchPlayer1().getId().equals(playerId)) {
//            postedMatch.getTeam2().getMatchPlayer1().setStatus(matchStatus);
//            return;
//        }
//        if (postedMatch.getTeam2().getMatchPlayer2().getId().equals(playerId)) {
//            postedMatch.getTeam2().getMatchPlayer2().setStatus(matchStatus);
//            return;
//        }
//
//        throw new RuntimeException("Player is not part of the given match");
//    }
//
//    private void approveMatchIfAllPlayersApproved(Match postedMatch) {
//        if (postedMatch.getTeam1().getMatchPlayer1().getStatus().equals(MatchStatus.APPROVED) &&
//                postedMatch.getTeam1().getMatchPlayer2().getStatus().equals(MatchStatus.APPROVED) &&
//                postedMatch.getTeam2().getMatchPlayer1().getStatus().equals(MatchStatus.APPROVED) &&
//                postedMatch.getTeam2().getMatchPlayer2().getStatus().equals(MatchStatus.APPROVED)) {
//            postedMatch.setStatus(MatchStatus.APPROVED);
//        }
//    }
//
//    private void validateNoPlayerIsOnTwoMatches(MatchInput input, List<Match> matches, Match postedMatch) {
//        var playerSet = validateFourDistinctPlayers(input);
//
//        if (matches.stream().filter(m -> !m.getId().equals(postedMatch.getId()))
//                .anyMatch(m -> isAnyPlayerOnTeam(m.getTeam1(), playerSet) || isAnyPlayerOnTeam(m.getTeam2(), playerSet))) {
//            throw new RuntimeException("A player was found on another match during the same time");
//        }
//    }
//
//    private Set<String> validateFourDistinctPlayers(MatchInput input) {
//        return Set.of(input.getTeam1().getMatchPlayer1(),
//                input.getTeam1().getMatchPlayer2(),
//                input.getTeam2().getMatchPlayer1(),
//                input.getTeam2().getMatchPlayer2());
//    }
//
//    private Match buildMatchObject(MatchInput input) {
//        return mapper.toMatch(UUID.randomUUID().toString(), MatchStatus.PENDING, input,
//                getOrOnboardPlayer(input.getTeam1().getMatchPlayer1()),
//                getOrOnboardPlayer(input.getTeam1().getMatchPlayer2()),
//                getOrOnboardPlayer(input.getTeam2().getMatchPlayer1()),
//                getOrOnboardPlayer(input.getTeam2().getMatchPlayer2()));
//    }
//
//    private Optional<Match> findMatchWithAllPlayers(MatchInput input, List<Match> matches) {
//        var filteredMatched = matches.stream()
//                .filter(match ->
//                        (areAllPlayersOnTeam(match.getTeam1(), Set.of(input.getTeam1().getMatchPlayer1(), input.getTeam1().getMatchPlayer2())) &&
//                                areAllPlayersOnTeam(match.getTeam2(), Set.of(input.getTeam2().getMatchPlayer1(), input.getTeam2().getMatchPlayer2())))
//                                ||
//                                (areAllPlayersOnTeam(match.getTeam1(), Set.of(input.getTeam2().getMatchPlayer1(), input.getTeam2().getMatchPlayer2()))
//                                        && areAllPlayersOnTeam(match.getTeam2(), Set.of(input.getTeam1().getMatchPlayer1(), input.getTeam1().getMatchPlayer2()))))
//                .toList();
//
//        if (filteredMatched.size() > 1) {
//            throw new RuntimeException("Too many matches");
//        }
//
//        return filteredMatched.size() == 1 ? Optional.of(filteredMatched.getFirst()) : Optional.empty();
//    }
//
//    private MatchPlayer getOrOnboardPlayer(String playerId) {
//        return mapper.toMatchPlayer(
//                playerService.findById(playerId).orElseGet(() -> playerService.createPlayer(playerId)),
//                MatchStatus.PENDING);
//    }


}
