package com.turminaz.myratingapp.match;

import com.opencsv.bean.CsvToBeanBuilder;
import com.turminaz.myratingapp.Topics;
import com.turminaz.myratingapp.config.AuthenticationFacade;
import com.turminaz.myratingapp.model.Match;
import com.turminaz.myratingapp.model.MatchPlayer;
import com.turminaz.myratingapp.model.MatchStatus;
import com.turminaz.myratingapp.player.PlayerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.turminaz.myratingapp.utils.MatchUtils.isMatchResultValid;

@Service
@Slf4j
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository repository;
    private final PlayerService playerService;
    private final AuthenticationFacade authenticationFacade;
    private final MatchMapper mapper;
    private final JmsTemplate jmsTemplate;

    public void republishedApprovedMatches() {
        repository.findAllByStatus(MatchStatus.APPROVED)
                .forEach(match -> jmsTemplate.convertAndSend(Topics.MATCH_CREATED, match));
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
                .peek(match -> match.getPlayers().forEach(matchPlayer -> updateMatchPlayerDetails(matchPlayer, authenticatedUserUid.getUid() )))
                .peek(match -> updateMatchStatus(match, isAdmin))
                .map(repository::save)
                .peek(match -> {
                    if (match.getStatus() == MatchStatus.APPROVED)
                        jmsTemplate.convertAndSend(Topics.MATCH_CREATED, match);
                })
                .map(mapper::toMatchDto)
                .toList();
    }

    private void updateMatchPlayerDetails(MatchPlayer matchPlayer, String authenticatedUserUid) {
        var player = playerService.findByIdOrCreate(matchPlayer.getId());
        matchPlayer.setName(player.getName());
        matchPlayer.setStatus(
                player.getUserUid() != null && player.getUserUid().equals(authenticatedUserUid)
                        ? MatchStatus.APPROVED
                        : MatchStatus.PENDING);
    }

    private void updateMatchStatus(Match match, boolean postedByAdmin) {
        var players = match.getPlayers().stream().map(MatchPlayer::getId).collect(Collectors.toSet());
        if (players.size() != 4) {
            match.setStatus(MatchStatus.REJECTED);
            match.setRejectedReason("Four distinct players are needed");
        } else {
            var validMatch = repository.findAllByStartTimeGreaterThan(match.getStartTime().minus(1, ChronoUnit.HOURS))
                    .stream()
                    .noneMatch(m -> m.getPlayers().stream().map(MatchPlayer::getId).anyMatch(players::contains));
            match.setStatus(validMatch && isMatchResultValid(match)
                    ? MatchStatus.PENDING : MatchStatus.REJECTED);
            match.setRejectedReason(validMatch
                    ? null : "Another match already exists for the same time or future");
            if (match.getStatus().equals(MatchStatus.PENDING) && postedByAdmin){
                log.info("Match {} approved", match);
                match.setStatus(MatchStatus.APPROVED);
            }
        }
    }
}
