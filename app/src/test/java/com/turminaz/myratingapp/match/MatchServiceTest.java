package com.turminaz.myratingapp.match;

import com.netflix.dgs.codegen.generated.types.MatchInput;
import com.netflix.dgs.codegen.generated.types.MatchStatusEnum;
import com.turminaz.myratingapp.config.AuthenticationFacade;
import com.turminaz.myratingapp.model.Player;
import com.turminaz.myratingapp.player.PlayerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.time.Instant;
import java.util.Optional;

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
    private AuthenticationFacade authenticationFacade;

    @InjectMocks
    private MatchService sut;
    private final PodamFactory podamFactory = new PodamFactoryImpl();

    private MatchInput input;

    @BeforeEach
    void setUp() {
        sut = new MatchService(repository, playerService, authenticationFacade, MatchMapper.INSTANCE);
        input = podamFactory.manufacturePojo(MatchInput.class);

    }

    @Test
    @DisplayName("Should create a match, when a matchInput is received")
    void createMatch() {
        //given
        when(authenticationFacade.authenticatedUserId()).thenReturn(input.getTeam1().getMatchPlayer1());
        when(repository.save(any(Match.class))).thenAnswer(i -> Mono.just(i.getArguments()[0]));
        when(repository.findAllByStartTime(any(Instant.class))).then(i -> Flux.empty());
        when(playerService.findById(anyString())).thenAnswer(i -> Optional.of(new Player().setId((String) i.getArguments()[0])));

        //when
        var result = sut.createMatch(input);

        //then
        assertThat(result.getId()).isNotBlank();
        assertThat(result.getStartTime()).isEqualTo(input.getStartTime());
        assertThat(result.getStatus()).isEqualTo(MatchStatusEnum.PENDING);

        assertThat(result.getTeam1().getMatchPlayer1().getId()).isEqualTo(input.getTeam1().getMatchPlayer1());
        assertThat(result.getTeam1().getMatchPlayer1().getStatus()).isEqualTo(MatchStatusEnum.APPROVED);

        assertThat(result.getTeam1().getMatchPlayer2().getId()).isEqualTo(input.getTeam1().getMatchPlayer2());
        assertThat(result.getTeam1().getMatchPlayer2().getStatus()).isEqualTo(MatchStatusEnum.PENDING);

        assertThat(result.getTeam2().getMatchPlayer1().getId()).isEqualTo(input.getTeam2().getMatchPlayer1());
        assertThat(result.getTeam2().getMatchPlayer1().getStatus()).isEqualTo(MatchStatusEnum.PENDING);

        assertThat(result.getTeam2().getMatchPlayer2().getId()).isEqualTo(input.getTeam2().getMatchPlayer2());
        assertThat(result.getTeam2().getMatchPlayer2().getStatus()).isEqualTo(MatchStatusEnum.PENDING);

        assertThat(result.getSetsPlayed()).usingRecursiveComparison().isEqualTo(input.getSetsPlayed());

        verify(repository).save(any(Match.class));
        verify(repository).findAllByStartTime(any(Instant.class));
        verify(playerService, times(4)).findById(anyString());
        verify(authenticationFacade).authenticatedUserId();

    }

    @ParameterizedTest
    @CsvSource({"APPROVED", "PENDING"})
    @DisplayName("Should update players approval, when a match already exists")
    void duplicatedMatch(MatchStatus player2ExistingStatus) {

        //given
        when(authenticationFacade.authenticatedUserId()).thenReturn(input.getTeam1().getMatchPlayer1());
        when(repository.save(any(Match.class))).thenAnswer(i -> Mono.just(i.getArguments()[0]));
        Match existingMatch = podamFactory.manufacturePojo(Match.class);
        existingMatch.setStartTime(input.getStartTime().toInstant());
        existingMatch.setStatus(MatchStatus.PENDING);

        existingMatch.getTeam1().getMatchPlayer1().setId(input.getTeam1().getMatchPlayer1());
        existingMatch.getTeam1().getMatchPlayer1().setStatus(MatchStatus.PENDING);

        existingMatch.getTeam1().getMatchPlayer2().setId(input.getTeam1().getMatchPlayer2());
        existingMatch.getTeam1().getMatchPlayer2().setStatus(player2ExistingStatus);

        existingMatch.getTeam2().getMatchPlayer1().setId(input.getTeam2().getMatchPlayer1());
        existingMatch.getTeam2().getMatchPlayer1().setStatus(MatchStatus.PENDING);

        existingMatch.getTeam2().getMatchPlayer2().setId(input.getTeam2().getMatchPlayer2());
        existingMatch.getTeam2().getMatchPlayer2().setStatus(MatchStatus.PENDING);

        when(repository.findAllByStartTime(any(Instant.class))).then(i -> Flux.just(existingMatch));

        //when
        var result = sut.createMatch(input);

        //then
        assertThat(result.getId()).isNotBlank();
        assertThat(result.getStartTime().toInstant()).isEqualTo(existingMatch.getStartTime());
        assertThat(result.getStatus()).isEqualTo(MatchStatusEnum.PENDING);

        assertThat(result.getTeam1().getMatchPlayer1().getId()).isEqualTo(existingMatch.getTeam1().getMatchPlayer1().getId());
        assertThat(result.getTeam1().getMatchPlayer1().getStatus()).isEqualTo(MatchStatusEnum.APPROVED);

        assertThat(result.getTeam1().getMatchPlayer2().getId()).isEqualTo(existingMatch.getTeam1().getMatchPlayer2().getId());
        assertThat(result.getTeam1().getMatchPlayer2().getStatus()).isEqualTo(MatchStatusEnum.valueOf(player2ExistingStatus.name()));

        assertThat(result.getTeam2().getMatchPlayer1().getId()).isEqualTo(existingMatch.getTeam2().getMatchPlayer1().getId());
        assertThat(result.getTeam2().getMatchPlayer1().getStatus()).isEqualTo(MatchStatusEnum.PENDING);

        assertThat(result.getTeam2().getMatchPlayer2().getId()).isEqualTo(existingMatch.getTeam2().getMatchPlayer2().getId());
        assertThat(result.getTeam2().getMatchPlayer2().getStatus()).isEqualTo(MatchStatusEnum.PENDING);

        assertThat(result.getSetsPlayed()).usingRecursiveComparison().isEqualTo(existingMatch.getSetsPlayed());

        verify(repository).save(any(Match.class));
        verify(repository).findAllByStartTime(any(Instant.class));
        verify(playerService, times(4)).findById(anyString());
        verify(authenticationFacade).authenticatedUserId();
    }

    @ParameterizedTest
    @CsvSource({"APPROVED", "PENDING"})
    void playerOnTwoMatchesAtTheSameTime(MatchStatus player1ExistingStatus) {
        //given
        var existingMatch = podamFactory.manufacturePojo(Match.class);
        existingMatch.setStartTime(input.getStartTime().toInstant());

        existingMatch.getTeam1().getMatchPlayer1().setId(input.getTeam1().getMatchPlayer1());
        existingMatch.getTeam1().getMatchPlayer1().setStatus(player1ExistingStatus);

        when(repository.findAllByStartTime(any(Instant.class))).then(i -> Flux.just(existingMatch));

        //when
        assertThatThrownBy(() -> sut.createMatch(input))
                .isInstanceOf(RuntimeException.class)
                .hasMessageStartingWith("A player was found on another match during the same time");

        verify(repository, times(0)).save(any(Match.class));
        verify(repository).findAllByStartTime(any(Instant.class));
        verifyNoInteractions(authenticationFacade);
        verify(playerService, times(4)).findById(anyString());
    }

    @Test
    @DisplayName("Should set match to APPROVED, when all players posted the same match")
    void multiplePostsForSameMatch() {

        //given
        when(authenticationFacade.authenticatedUserId()).thenReturn(input.getTeam1().getMatchPlayer1());
        when(repository.save(any(Match.class))).thenAnswer(i -> Mono.just(i.getArguments()[0]));
        Match existingMatch = podamFactory.manufacturePojo(Match.class);
        existingMatch.setStartTime(input.getStartTime().toInstant());
        existingMatch.setStatus(MatchStatus.PENDING);

        existingMatch.getTeam1().getMatchPlayer1().setId(input.getTeam1().getMatchPlayer1());
        existingMatch.getTeam1().getMatchPlayer1().setStatus(MatchStatus.PENDING);

        existingMatch.getTeam1().getMatchPlayer2().setId(input.getTeam1().getMatchPlayer2());
        existingMatch.getTeam1().getMatchPlayer2().setStatus(MatchStatus.APPROVED);

        existingMatch.getTeam2().getMatchPlayer1().setId(input.getTeam2().getMatchPlayer1());
        existingMatch.getTeam2().getMatchPlayer1().setStatus(MatchStatus.APPROVED);

        existingMatch.getTeam2().getMatchPlayer2().setId(input.getTeam2().getMatchPlayer2());
        existingMatch.getTeam2().getMatchPlayer2().setStatus(MatchStatus.APPROVED);

        when(repository.findAllByStartTime(any(Instant.class))).then(i -> Flux.just(existingMatch));

        //when
        var result = sut.createMatch(input);

        //then
        assertThat(result.getId()).isNotBlank();
        assertThat(result.getStartTime().toInstant()).isEqualTo(existingMatch.getStartTime());

        assertThat(result.getTeam1().getMatchPlayer1().getId()).isEqualTo(existingMatch.getTeam1().getMatchPlayer1().getId());
        assertThat(result.getTeam1().getMatchPlayer1().getStatus()).isEqualTo(MatchStatusEnum.APPROVED);

        assertThat(result.getTeam1().getMatchPlayer2().getId()).isEqualTo(existingMatch.getTeam1().getMatchPlayer2().getId());
        assertThat(result.getTeam1().getMatchPlayer2().getStatus()).isEqualTo(MatchStatusEnum.APPROVED);

        assertThat(result.getTeam2().getMatchPlayer1().getId()).isEqualTo(existingMatch.getTeam2().getMatchPlayer1().getId());
        assertThat(result.getTeam2().getMatchPlayer1().getStatus()).isEqualTo(MatchStatusEnum.APPROVED);

        assertThat(result.getTeam2().getMatchPlayer2().getId()).isEqualTo(existingMatch.getTeam2().getMatchPlayer2().getId());
        assertThat(result.getTeam2().getMatchPlayer2().getStatus()).isEqualTo(MatchStatusEnum.APPROVED);

        assertThat(result.getStatus()).isEqualTo(MatchStatusEnum.APPROVED);
        assertThat(result.getSetsPlayed()).usingRecursiveComparison().isEqualTo(existingMatch.getSetsPlayed());

        verify(repository).save(any(Match.class));
        verify(repository).findAllByStartTime(any(Instant.class));
        verify(playerService, times(4)).findById(anyString());
        verify(authenticationFacade).authenticatedUserId();

    }

    @ParameterizedTest
    @DisplayName("Should set match to REJECTED, when one player rejects the match")
    @CsvSource({"PENDING", "REJECTED"})
    void rejectMatch(MatchStatus existingMatchStatus) {

        //given

        when(repository.save(any(Match.class))).thenAnswer(i -> Mono.just(i.getArguments()[0]));
        Match existingMatch = podamFactory.manufacturePojo(Match.class);

        when(authenticationFacade.authenticatedUserId()).thenReturn(existingMatch.getTeam1().getMatchPlayer1().getId());

        existingMatch.setStatus(existingMatchStatus);

        existingMatch.getTeam1().getMatchPlayer1().setStatus(MatchStatus.PENDING);
        existingMatch.getTeam1().getMatchPlayer2().setStatus(MatchStatus.APPROVED);
        existingMatch.getTeam2().getMatchPlayer1().setStatus(MatchStatus.APPROVED);
        existingMatch.getTeam2().getMatchPlayer2().setStatus(MatchStatus.APPROVED);

        when(repository.findById(anyString())).then(i -> Mono.just(existingMatch));

        //when
        var result = sut.rejectMatch(existingMatch.getId());

        //then
        assertThat(result.getId()).isNotBlank();
        assertThat(result.getStartTime().toInstant()).isEqualTo(existingMatch.getStartTime());

        assertThat(result.getTeam1().getMatchPlayer1().getId()).isEqualTo(existingMatch.getTeam1().getMatchPlayer1().getId());
        assertThat(result.getTeam1().getMatchPlayer1().getStatus()).isEqualTo(MatchStatusEnum.REJECTED);

        assertThat(result.getTeam1().getMatchPlayer2().getId()).isEqualTo(existingMatch.getTeam1().getMatchPlayer2().getId());
        assertThat(result.getTeam1().getMatchPlayer2().getStatus()).isEqualTo(MatchStatusEnum.APPROVED);

        assertThat(result.getTeam2().getMatchPlayer1().getId()).isEqualTo(existingMatch.getTeam2().getMatchPlayer1().getId());
        assertThat(result.getTeam2().getMatchPlayer1().getStatus()).isEqualTo(MatchStatusEnum.APPROVED);

        assertThat(result.getTeam2().getMatchPlayer2().getId()).isEqualTo(existingMatch.getTeam2().getMatchPlayer2().getId());
        assertThat(result.getTeam2().getMatchPlayer2().getStatus()).isEqualTo(MatchStatusEnum.APPROVED);

        assertThat(result.getStatus()).isEqualTo(MatchStatusEnum.REJECTED);
        assertThat(result.getSetsPlayed()).usingRecursiveComparison().isEqualTo(existingMatch.getSetsPlayed());

        verify(repository).save(any(Match.class));
        verify(repository).findById(anyString());
        verifyNoInteractions(playerService);
        verify(authenticationFacade).authenticatedUserId();
    }

    @ParameterizedTest
    @DisplayName("Should set match to REJECTED, when one player rejects the match")
    @CsvSource({"PENDING", "REJECTED"})
    void approveMatch(MatchStatus existingMatchStatus) {

        //given

        when(repository.save(any(Match.class))).thenAnswer(i -> Mono.just(i.getArguments()[0]));
        Match existingMatch = podamFactory.manufacturePojo(Match.class);

        when(authenticationFacade.authenticatedUserId()).thenReturn(existingMatch.getTeam1().getMatchPlayer1().getId());

        existingMatch.setStatus(existingMatchStatus);

        existingMatch.getTeam1().getMatchPlayer1().setStatus(MatchStatus.PENDING);
        existingMatch.getTeam1().getMatchPlayer2().setStatus(MatchStatus.APPROVED);
        existingMatch.getTeam2().getMatchPlayer1().setStatus(MatchStatus.APPROVED);
        existingMatch.getTeam2().getMatchPlayer2().setStatus(MatchStatus.APPROVED);

        when(repository.findById(anyString())).then(i -> Mono.just(existingMatch));

        //when
        var result = sut.approveMatch(existingMatch.getId());

        //then
        assertThat(result.getId()).isNotBlank();
        assertThat(result.getStartTime().toInstant()).isEqualTo(existingMatch.getStartTime());

        assertThat(result.getTeam1().getMatchPlayer1().getId()).isEqualTo(existingMatch.getTeam1().getMatchPlayer1().getId());
        assertThat(result.getTeam1().getMatchPlayer1().getStatus()).isEqualTo(MatchStatusEnum.APPROVED);

        assertThat(result.getTeam1().getMatchPlayer2().getId()).isEqualTo(existingMatch.getTeam1().getMatchPlayer2().getId());
        assertThat(result.getTeam1().getMatchPlayer2().getStatus()).isEqualTo(MatchStatusEnum.APPROVED);

        assertThat(result.getTeam2().getMatchPlayer1().getId()).isEqualTo(existingMatch.getTeam2().getMatchPlayer1().getId());
        assertThat(result.getTeam2().getMatchPlayer1().getStatus()).isEqualTo(MatchStatusEnum.APPROVED);

        assertThat(result.getTeam2().getMatchPlayer2().getId()).isEqualTo(existingMatch.getTeam2().getMatchPlayer2().getId());
        assertThat(result.getTeam2().getMatchPlayer2().getStatus()).isEqualTo(MatchStatusEnum.APPROVED);

        assertThat(result.getStatus()).isEqualTo(MatchStatusEnum.APPROVED);
        assertThat(result.getSetsPlayed()).usingRecursiveComparison().isEqualTo(existingMatch.getSetsPlayed());

        verify(repository).save(any(Match.class));
        verify(repository).findById(anyString());
        verifyNoInteractions(playerService);
        verify(authenticationFacade).authenticatedUserId();
    }


    @ParameterizedTest
    @CsvSource({"111,222,333,333",
            "111,333,333,333",
            "333,333,333,333",
            "111,111,333,333",
            "111,222,111,333",
            "111,222,111,2222",
    })
    void duplicatedPlayer(String player1, String player2, String player3, String player4) {
        //given
        input.getTeam1().setMatchPlayer1(player1);
        input.getTeam1().setMatchPlayer2(player2);
        input.getTeam2().setMatchPlayer1(player3);
        input.getTeam2().setMatchPlayer2(player4);

        //when
        assertThatThrownBy(() -> sut.createMatch(input))
                .isInstanceOf(RuntimeException.class)
                .hasMessageStartingWith("duplicate element");


        //then
        verifyNoInteractions(repository);
        verifyNoInteractions(playerService);
        verifyNoInteractions(authenticationFacade);

    }


}