package com.turminaz.myratingapp.match;


import com.turminaz.myratingapp.playerMatchService.PlayerMatchService;
import com.turminaz.myratingapp.config.AuthenticationFacade;
import com.turminaz.myratingapp.model.Match;
import com.turminaz.myratingapp.dto.MatchPlayerDto;
import com.turminaz.myratingapp.model.MatchStatus;
import com.turminaz.myratingapp.model.Player;
import com.turminaz.myratingapp.player.PlayerService;
import com.turminaz.myratingapp.rating.EloRatingService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.turminaz.myratingapp.match.MatchTestUtils.p1t1;
import static com.turminaz.myratingapp.match.MatchTestUtils.p1t2;
import static com.turminaz.myratingapp.match.MatchTestUtils.p2t1;
import static com.turminaz.myratingapp.match.MatchTestUtils.p2t2;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchServiceTest {

    @Mock
    private MatchRepository repository;
    @Mock
    private PlayerService playerService;

    @Mock
    private EloRatingService eloRatingService;

    @Mock
    private AuthenticationFacade authenticationFacade;

    @Mock
    PlayerMatchService playerMatchService;

    @InjectMocks
    private MatchService sut;

    private PostMatchDto postMatchDto;

    @BeforeEach
    void setUp() {
        sut = new MatchService(repository, playerService, eloRatingService, authenticationFacade, MatchMapper.INSTANCE, playerMatchService);
        postMatchDto = new PostMatchDto();
        postMatchDto.setStartTime(LocalDateTime.now().minusDays(1));
        postMatchDto.setScores(List.of(new SetScoreDto(5, 7), new SetScoreDto(5, 7)));
        postMatchDto.setTeam1Player1(p1t1);
        postMatchDto.setTeam1Player2(p1t2);
        postMatchDto.setTeam2Player1(p2t1);
        postMatchDto.setTeam2Player2(p2t2);

    }

    @Test
    @DisplayName("Should create a PENDING match, when a matchInput is posted by non-admin")
    void createMatch() {
        //given
        when(authenticationFacade.isAdmin()).thenReturn(false);
        when(authenticationFacade.getUserUid()).thenReturn(postMatchDto.getTeam1Player1());
        when(repository.save(any(Match.class))).thenAnswer(i -> ((Match) i.getArguments()[0]).setId(new ObjectId().toString()));
        when(repository.findAllByStartTimeGreaterThan(any(Instant.class))).then(i -> emptyList());
        when(playerService.findById(anyString())).thenAnswer(i -> new Player()
                .setId(new ObjectId(String.valueOf(i.getArguments()[0])))
                .setUserUid(String.valueOf(i.getArguments()[0]))
        );

        //when
        var result = sut.postMatch(postMatchDto);

        //then
        assertThat(result.getId()).isNotBlank();
        assertThat(result.getStartTime()).isEqualTo(postMatchDto.getStartTime());
        assertThat(result.getStatus()).isEqualTo(MatchStatus.PENDING);


        assertThat(result.getPlayers().stream().map(MatchPlayerDto::getId).collect(Collectors.toSet()).contains(postMatchDto.getTeam1Player1())).isTrue();
        assertThat(result.getPlayers().stream().map(MatchPlayerDto::getId).collect(Collectors.toSet()).contains(postMatchDto.getTeam1Player2())).isTrue();
        assertThat(result.getPlayers().stream().map(MatchPlayerDto::getId).collect(Collectors.toSet()).contains(postMatchDto.getTeam2Player1())).isTrue();
        assertThat(result.getPlayers().stream().map(MatchPlayerDto::getId).collect(Collectors.toSet()).contains(postMatchDto.getTeam2Player2())).isTrue();

        assertThat(result.getPlayers().stream().map(MatchPlayerDto::getStatus).collect(Collectors.toList())).containsExactlyInAnyOrder(
                MatchStatus.APPROVED, MatchStatus.PENDING, MatchStatus.PENDING, MatchStatus.PENDING);

        assertThat(result.getScores()).usingRecursiveComparison().isEqualTo(result.getScores());

        verify(repository).save(any(Match.class));
        verify(repository).findAllByStartTimeGreaterThan(any(Instant.class));
        verify(playerService, times(4)).findById(anyString());
        verify(authenticationFacade).isAdmin();
        verify(authenticationFacade, times(4)).getUserUid();

    }

    @Test
    void approveMatch() {

        //given
        var match = MatchTestUtils.createMatch();
        when(repository.findById(anyString())).thenReturn(Optional.of(
                match));
        when(repository.save(any(Match.class))).thenAnswer(i -> ((Match) i.getArguments()[0]).setId(new ObjectId().toString()));
        when(authenticationFacade.getUserUid()).thenReturn(p1t2);
        when(playerService.findByUserUidOrOnboard(anyString())).thenReturn(Optional.ofNullable(new Player()
                .setId(new ObjectId(p1t2))
                .setUserUid(p1t2))
        );

        //when
        var matchDto = sut.approve(match.getId());
        var players = matchDto.getPlayers();

        //then
        assertThat(matchDto.getStatus()).isEqualTo(MatchStatus.PENDING);
        assertThat(players.stream().map(MatchPlayerDto::getStatus).collect(Collectors.toList())).containsExactlyInAnyOrder(
                MatchStatus.APPROVED, MatchStatus.APPROVED, MatchStatus.PENDING, MatchStatus.PENDING);

        assertThat(players.stream().filter(matchPlayerDto -> matchPlayerDto.getId().equals(p1t2))
                .toList().getFirst().getStatus()).isEqualTo(MatchStatus.APPROVED);

        verify(repository).findById(anyString());
        verify(repository).save(any(Match.class));
        verify(playerService).findByUserUidOrOnboard(p1t2);
        verify(authenticationFacade).isAdmin();
        verify(authenticationFacade).getUserUid();


    }

    @Test
    @DisplayName("Should not change match status when the match was previously rejected")
    void approveRejectedMatch() {

        //given
        var match = MatchTestUtils.createMatch().setStatus(MatchStatus.REJECTED);
        match.getPlayers().get(1).setStatus(MatchStatus.REJECTED);

        when(repository.findById(anyString())).thenReturn(Optional.of(match));

        when(repository.save(any(Match.class))).thenAnswer(i -> ((Match) i.getArguments()[0]).setId(new ObjectId().toString()));
        when(authenticationFacade.getUserUid()).thenReturn(p1t2);
        when(playerService.findByUserUidOrOnboard(anyString())).thenReturn(Optional.ofNullable(new Player()
                .setId(new ObjectId(p1t2))
                .setUserUid(p1t2))
        );

        //when
        var matchDto = sut.approve(match.getId());

        //then
        var players = matchDto.getPlayers();
        assertThat(matchDto.getStatus()).isEqualTo(MatchStatus.REJECTED);
        assertThat(players.stream().map(MatchPlayerDto::getStatus).collect(Collectors.toList())).containsExactlyInAnyOrder(
                MatchStatus.APPROVED, MatchStatus.REJECTED, MatchStatus.APPROVED, MatchStatus.PENDING);

        assertThat(players.stream().filter(matchPlayerDto -> matchPlayerDto.getId().equals(p1t2))
                .toList().getFirst().getStatus()).isEqualTo(MatchStatus.APPROVED);

        verify(repository).findById(anyString());
        verify(repository).save(any(Match.class));
        verify(playerService).findByUserUidOrOnboard(p1t2);
        verify(authenticationFacade).isAdmin();
        verify(authenticationFacade).getUserUid();

    }

    @Test
    void rejectMatch() {
        //given
        var match = MatchTestUtils.createMatch();
        match.getPlayers().get(1).setStatus(MatchStatus.APPROVED);
        match.getPlayers().get(3).setStatus(MatchStatus.APPROVED);

        when(repository.findById(anyString())).thenReturn(Optional.of(match));

        when(authenticationFacade.getUserUid()).thenReturn(p1t2);
        when(repository.save(any(Match.class))).thenAnswer(i -> ((Match) i.getArguments()[0]).setId(new ObjectId().toString()));
        when(playerService.findByUserUidOrOnboard(anyString())).thenReturn(Optional.ofNullable(new Player()
                .setId(new ObjectId(p1t2))
                .setUserUid(p1t2))
        );


        //when
        var matchDto = sut.reject(match.getId());

        //then
        var players = matchDto.getPlayers();
        assertThat(matchDto.getStatus()).isEqualTo(MatchStatus.REJECTED);
        assertThat(players.stream().map(MatchPlayerDto::getStatus).collect(Collectors.toList())).containsExactlyInAnyOrder(
                MatchStatus.APPROVED, MatchStatus.APPROVED, MatchStatus.REJECTED, MatchStatus.APPROVED);

        assertThat(players.stream().filter(matchPlayerDto -> matchPlayerDto.getId().equals(p1t2))
                .toList().getFirst().getStatus()).isEqualTo(MatchStatus.REJECTED);

        verify(repository).findById(anyString());
        verify(repository).save(any(Match.class));
        verify(playerService).findByUserUidOrOnboard(p1t2);
        verify(authenticationFacade).isAdmin();
        verify(authenticationFacade).getUserUid();

    }

    @Test
    @DisplayName("Should throw error when player not on match")
    void throwErrorIfPlayerNotOnMatch() {
        //given
        var match = MatchTestUtils.createMatch();
        match.getPlayers().get(1).setStatus(MatchStatus.APPROVED);
        match.getPlayers().get(3).setStatus(MatchStatus.APPROVED);

        when(repository.findById(anyString())).thenReturn(Optional.of(match));

        var otherPlayerId = new ObjectId().toString();
        when(authenticationFacade.getUserUid()).thenReturn(otherPlayerId);

        when(playerService.findByUserUidOrOnboard(anyString())).thenReturn(Optional.ofNullable(new Player()
                .setId(new ObjectId(otherPlayerId))
                .setUserUid(otherPlayerId))
        );


        //then
        assertThatThrownBy(() -> sut.approve(match.getId())).isInstanceOf(RuntimeException.class);

        verify(repository).findById(anyString());
        verify(repository, times(0)).save(any(Match.class));
        verify(playerService).findByUserUidOrOnboard(otherPlayerId);
        verify(authenticationFacade).isAdmin();
        verify(authenticationFacade).getUserUid();

    }

    @Test
    @DisplayName("should update match status when is admin")
    void updateMatchStatusIfIsAdmin() {

        when(authenticationFacade.isAdmin()).thenReturn(true);

        var match = MatchTestUtils.createMatch();
        match.getPlayers().get(1).setStatus(MatchStatus.APPROVED);

        when(repository.findById(anyString())).thenReturn(Optional.of(match));

        when(authenticationFacade.getUserUid()).thenReturn(p1t2);
        when(repository.save(any(Match.class))).thenAnswer(i -> ((Match) i.getArguments()[0]).setId(new ObjectId().toString()));
        when(playerService.findByUserUidOrOnboard(anyString())).thenReturn(Optional.ofNullable(new Player()
                .setId(new ObjectId(p1t2))
                .setUserUid(p1t2))
        );


        //when
        var matchDto = sut.approve(match.getId());

        //then
        var players = matchDto.getPlayers();
        assertThat(matchDto.getStatus()).isEqualTo(MatchStatus.APPROVED);
        assertThat(players.stream().map(MatchPlayerDto::getStatus).collect(Collectors.toList())).containsExactlyInAnyOrder(
                MatchStatus.APPROVED, MatchStatus.APPROVED, MatchStatus.APPROVED, MatchStatus.PENDING);

        assertThat(players.stream().filter(matchPlayerDto -> matchPlayerDto.getId().equals(p1t2))
                .toList().getFirst().getStatus()).isEqualTo(MatchStatus.APPROVED);

        verify(repository).findById(anyString());
        verify(repository).save(any(Match.class));
        verify(playerService).findByUserUidOrOnboard(p1t2);
        verify(authenticationFacade).isAdmin();
        verify(authenticationFacade).getUserUid();
    }
//
//    @ParameterizedTest
//    @CsvSource({"APPROVED", "PENDING"})
//    @DisplayName("Should update players approval, when a match already exists")
//    void duplicatedMatch(MatchStatus player2ExistingStatus) {
//
//        //given
//        when(authenticationFacade.authenticatedUserId()).thenReturn(input.getTeam1().getMatchPlayer1());
//        when(repository.save(any(Match.class))).thenAnswer(i -> Mono.just(i.getArguments()[0]));
//        Match existingMatch = podamFactory.manufacturePojo(Match.class);
//        existingMatch.setStartTime(input.getStartTime().toInstant());
//        existingMatch.setStatus(MatchStatus.PENDING);
//
//        existingMatch.getTeam1().getMatchPlayer1().setId(input.getTeam1().getMatchPlayer1());
//        existingMatch.getTeam1().getMatchPlayer1().setStatus(MatchStatus.PENDING);
//
//        existingMatch.getTeam1().getMatchPlayer2().setId(input.getTeam1().getMatchPlayer2());
//        existingMatch.getTeam1().getMatchPlayer2().setStatus(player2ExistingStatus);
//
//        existingMatch.getTeam2().getMatchPlayer1().setId(input.getTeam2().getMatchPlayer1());
//        existingMatch.getTeam2().getMatchPlayer1().setStatus(MatchStatus.PENDING);
//
//        existingMatch.getTeam2().getMatchPlayer2().setId(input.getTeam2().getMatchPlayer2());
//        existingMatch.getTeam2().getMatchPlayer2().setStatus(MatchStatus.PENDING);
//
//        when(repository.findAllByStartTime(any(Instant.class))).then(i -> Flux.just(existingMatch));
//
//        //when
//        var result = sut.createMatch(input);
//
//        //then
//        assertThat(result.getId()).isNotBlank();
//        assertThat(result.getStartTime().toInstant()).isEqualTo(existingMatch.getStartTime());
//        assertThat(result.getStatus()).isEqualTo(MatchStatusEnum.PENDING);
//
//        assertThat(result.getTeam1().getMatchPlayer1().getId()).isEqualTo(existingMatch.getTeam1().getMatchPlayer1().getId());
//        assertThat(result.getTeam1().getMatchPlayer1().getStatus()).isEqualTo(MatchStatusEnum.APPROVED);
//
//        assertThat(result.getTeam1().getMatchPlayer2().getId()).isEqualTo(existingMatch.getTeam1().getMatchPlayer2().getId());
//        assertThat(result.getTeam1().getMatchPlayer2().getStatus()).isEqualTo(MatchStatusEnum.valueOf(player2ExistingStatus.name()));
//
//        assertThat(result.getTeam2().getMatchPlayer1().getId()).isEqualTo(existingMatch.getTeam2().getMatchPlayer1().getId());
//        assertThat(result.getTeam2().getMatchPlayer1().getStatus()).isEqualTo(MatchStatusEnum.PENDING);
//
//        assertThat(result.getTeam2().getMatchPlayer2().getId()).isEqualTo(existingMatch.getTeam2().getMatchPlayer2().getId());
//        assertThat(result.getTeam2().getMatchPlayer2().getStatus()).isEqualTo(MatchStatusEnum.PENDING);
//
//        assertThat(result.getSetsPlayed()).usingRecursiveComparison().isEqualTo(existingMatch.getSetsPlayed());
//
//        verify(repository).save(any(Match.class));
//        verify(repository).findAllByStartTime(any(Instant.class));
//        verify(playerService, times(4)).findById(anyString());
//        verify(authenticationFacade).authenticatedUserId();
//    }
//
//    @ParameterizedTest
//    @CsvSource({"APPROVED", "PENDING"})
//    void playerOnTwoMatchesAtTheSameTime(MatchStatus player1ExistingStatus) {
//        //given
//        var existingMatch = podamFactory.manufacturePojo(Match.class);
//        existingMatch.setStartTime(input.getStartTime().toInstant());
//
//        existingMatch.getTeam1().getMatchPlayer1().setId(input.getTeam1().getMatchPlayer1());
//        existingMatch.getTeam1().getMatchPlayer1().setStatus(player1ExistingStatus);
//
//        when(repository.findAllByStartTime(any(Instant.class))).then(i -> Flux.just(existingMatch));
//
//        //when
//        assertThatThrownBy(() -> sut.createMatch(input))
//                .isInstanceOf(RuntimeException.class)
//                .hasMessageStartingWith("A player was found on another match during the same time");
//
//        verify(repository, times(0)).save(any(Match.class));
//        verify(repository).findAllByStartTime(any(Instant.class));
//        verifyNoInteractions(authenticationFacade);
//        verify(playerService, times(4)).findById(anyString());
//    }
//
//    @Test
//    @DisplayName("Should set match to APPROVED, when all players posted the same match")
//    void multiplePostsForSameMatch() {
//
//        //given
//        when(authenticationFacade.authenticatedUserId()).thenReturn(input.getTeam1().getMatchPlayer1());
//        when(repository.save(any(Match.class))).thenAnswer(i -> Mono.just(i.getArguments()[0]));
//        Match existingMatch = podamFactory.manufacturePojo(Match.class);
//        existingMatch.setStartTime(input.getStartTime().toInstant());
//        existingMatch.setStatus(MatchStatus.PENDING);
//
//        existingMatch.getTeam1().getMatchPlayer1().setId(input.getTeam1().getMatchPlayer1());
//        existingMatch.getTeam1().getMatchPlayer1().setStatus(MatchStatus.PENDING);
//
//        existingMatch.getTeam1().getMatchPlayer2().setId(input.getTeam1().getMatchPlayer2());
//        existingMatch.getTeam1().getMatchPlayer2().setStatus(MatchStatus.APPROVED);
//
//        existingMatch.getTeam2().getMatchPlayer1().setId(input.getTeam2().getMatchPlayer1());
//        existingMatch.getTeam2().getMatchPlayer1().setStatus(MatchStatus.APPROVED);
//
//        existingMatch.getTeam2().getMatchPlayer2().setId(input.getTeam2().getMatchPlayer2());
//        existingMatch.getTeam2().getMatchPlayer2().setStatus(MatchStatus.APPROVED);
//
//        when(repository.findAllByStartTime(any(Instant.class))).then(i -> Flux.just(existingMatch));
//
//        //when
//        var result = sut.createMatch(input);
//
//        //then
//        assertThat(result.getId()).isNotBlank();
//        assertThat(result.getStartTime().toInstant()).isEqualTo(existingMatch.getStartTime());
//
//        assertThat(result.getTeam1().getMatchPlayer1().getId()).isEqualTo(existingMatch.getTeam1().getMatchPlayer1().getId());
//        assertThat(result.getTeam1().getMatchPlayer1().getStatus()).isEqualTo(MatchStatusEnum.APPROVED);
//
//        assertThat(result.getTeam1().getMatchPlayer2().getId()).isEqualTo(existingMatch.getTeam1().getMatchPlayer2().getId());
//        assertThat(result.getTeam1().getMatchPlayer2().getStatus()).isEqualTo(MatchStatusEnum.APPROVED);
//
//        assertThat(result.getTeam2().getMatchPlayer1().getId()).isEqualTo(existingMatch.getTeam2().getMatchPlayer1().getId());
//        assertThat(result.getTeam2().getMatchPlayer1().getStatus()).isEqualTo(MatchStatusEnum.APPROVED);
//
//        assertThat(result.getTeam2().getMatchPlayer2().getId()).isEqualTo(existingMatch.getTeam2().getMatchPlayer2().getId());
//        assertThat(result.getTeam2().getMatchPlayer2().getStatus()).isEqualTo(MatchStatusEnum.APPROVED);
//
//        assertThat(result.getStatus()).isEqualTo(MatchStatusEnum.APPROVED);
//        assertThat(result.getSetsPlayed()).usingRecursiveComparison().isEqualTo(existingMatch.getSetsPlayed());
//
//        verify(repository).save(any(Match.class));
//        verify(repository).findAllByStartTime(any(Instant.class));
//        verify(playerService, times(4)).findById(anyString());
//        verify(authenticationFacade).authenticatedUserId();
//
//    }
//
//    @ParameterizedTest
//    @DisplayName("Should set match to REJECTED, when one player rejects the match")
//    @CsvSource({"PENDING", "REJECTED"})
//    void rejectMatch(MatchStatus existingMatchStatus) {
//
//        //given
//
//        when(repository.save(any(Match.class))).thenAnswer(i -> Mono.just(i.getArguments()[0]));
//        Match existingMatch = podamFactory.manufacturePojo(Match.class);
//
//        when(authenticationFacade.authenticatedUserId()).thenReturn(existingMatch.getTeam1().getMatchPlayer1().getId());
//
//        existingMatch.setStatus(existingMatchStatus);
//
//        existingMatch.getTeam1().getMatchPlayer1().setStatus(MatchStatus.PENDING);
//        existingMatch.getTeam1().getMatchPlayer2().setStatus(MatchStatus.APPROVED);
//        existingMatch.getTeam2().getMatchPlayer1().setStatus(MatchStatus.APPROVED);
//        existingMatch.getTeam2().getMatchPlayer2().setStatus(MatchStatus.APPROVED);
//
//        when(repository.findById(anyString())).then(i -> Mono.just(existingMatch));
//
//        //when
//        var result = sut.rejectMatch(existingMatch.getId());
//
//        //then
//        assertThat(result.getId()).isNotBlank();
//        assertThat(result.getStartTime().toInstant()).isEqualTo(existingMatch.getStartTime());
//
//        assertThat(result.getTeam1().getMatchPlayer1().getId()).isEqualTo(existingMatch.getTeam1().getMatchPlayer1().getId());
//        assertThat(result.getTeam1().getMatchPlayer1().getStatus()).isEqualTo(MatchStatusEnum.REJECTED);
//
//        assertThat(result.getTeam1().getMatchPlayer2().getId()).isEqualTo(existingMatch.getTeam1().getMatchPlayer2().getId());
//        assertThat(result.getTeam1().getMatchPlayer2().getStatus()).isEqualTo(MatchStatusEnum.APPROVED);
//
//        assertThat(result.getTeam2().getMatchPlayer1().getId()).isEqualTo(existingMatch.getTeam2().getMatchPlayer1().getId());
//        assertThat(result.getTeam2().getMatchPlayer1().getStatus()).isEqualTo(MatchStatusEnum.APPROVED);
//
//        assertThat(result.getTeam2().getMatchPlayer2().getId()).isEqualTo(existingMatch.getTeam2().getMatchPlayer2().getId());
//        assertThat(result.getTeam2().getMatchPlayer2().getStatus()).isEqualTo(MatchStatusEnum.APPROVED);
//
//        assertThat(result.getStatus()).isEqualTo(MatchStatusEnum.REJECTED);
//        assertThat(result.getSetsPlayed()).usingRecursiveComparison().isEqualTo(existingMatch.getSetsPlayed());
//
//        verify(repository).save(any(Match.class));
//        verify(repository).findById(anyString());
//        verifyNoInteractions(playerService);
//        verify(authenticationFacade).authenticatedUserId();
//    }
//
//    @ParameterizedTest
//    @DisplayName("Should set match to REJECTED, when one player rejects the match")
//    @CsvSource({"PENDING", "REJECTED"})
//    void approveMatch(MatchStatus existingMatchStatus) {
//
//        //given
//
//        when(repository.save(any(Match.class))).thenAnswer(i -> Mono.just(i.getArguments()[0]));
//        Match existingMatch = podamFactory.manufacturePojo(Match.class);
//
//        when(authenticationFacade.authenticatedUserId()).thenReturn(existingMatch.getTeam1().getMatchPlayer1().getId());
//
//        existingMatch.setStatus(existingMatchStatus);
//
//        existingMatch.getTeam1().getMatchPlayer1().setStatus(MatchStatus.PENDING);
//        existingMatch.getTeam1().getMatchPlayer2().setStatus(MatchStatus.APPROVED);
//        existingMatch.getTeam2().getMatchPlayer1().setStatus(MatchStatus.APPROVED);
//        existingMatch.getTeam2().getMatchPlayer2().setStatus(MatchStatus.APPROVED);
//
//        when(repository.findById(anyString())).then(i -> Mono.just(existingMatch));
//
//        //when
//        var result = sut.approveMatch(existingMatch.getId());
//
//        //then
//        assertThat(result.getId()).isNotBlank();
//        assertThat(result.getStartTime().toInstant()).isEqualTo(existingMatch.getStartTime());
//
//        assertThat(result.getTeam1().getMatchPlayer1().getId()).isEqualTo(existingMatch.getTeam1().getMatchPlayer1().getId());
//        assertThat(result.getTeam1().getMatchPlayer1().getStatus()).isEqualTo(MatchStatusEnum.APPROVED);
//
//        assertThat(result.getTeam1().getMatchPlayer2().getId()).isEqualTo(existingMatch.getTeam1().getMatchPlayer2().getId());
//        assertThat(result.getTeam1().getMatchPlayer2().getStatus()).isEqualTo(MatchStatusEnum.APPROVED);
//
//        assertThat(result.getTeam2().getMatchPlayer1().getId()).isEqualTo(existingMatch.getTeam2().getMatchPlayer1().getId());
//        assertThat(result.getTeam2().getMatchPlayer1().getStatus()).isEqualTo(MatchStatusEnum.APPROVED);
//
//        assertThat(result.getTeam2().getMatchPlayer2().getId()).isEqualTo(existingMatch.getTeam2().getMatchPlayer2().getId());
//        assertThat(result.getTeam2().getMatchPlayer2().getStatus()).isEqualTo(MatchStatusEnum.APPROVED);
//
//        assertThat(result.getStatus()).isEqualTo(MatchStatusEnum.APPROVED);
//        assertThat(result.getSetsPlayed()).usingRecursiveComparison().isEqualTo(existingMatch.getSetsPlayed());
//
//        verify(repository).save(any(Match.class));
//        verify(repository).findById(anyString());
//        verifyNoInteractions(playerService);
//        verify(authenticationFacade).authenticatedUserId();
//    }
//
//
//    @ParameterizedTest
//    @CsvSource({"111,222,333,333",
//            "111,333,333,333",
//            "333,333,333,333",
//            "111,111,333,333",
//            "111,222,111,333",
//            "111,222,111,2222",
//    })
//    void duplicatedPlayer(String player1, String player2, String player3, String player4) {
//        //given
//        input.getTeam1().setMatchPlayer1(player1);
//        input.getTeam1().setMatchPlayer2(player2);
//        input.getTeam2().setMatchPlayer1(player3);
//        input.getTeam2().setMatchPlayer2(player4);
//
//        //when
//        assertThatThrownBy(() -> sut.createMatch(input))
//                .isInstanceOf(RuntimeException.class)
//                .hasMessageStartingWith("duplicate element");
//
//
//        //then
//        verifyNoInteractions(repository);
//        verifyNoInteractions(playerService);
//        verifyNoInteractions(authenticationFacade);
//
//    }


}