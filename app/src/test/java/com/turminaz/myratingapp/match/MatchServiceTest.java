package com.turminaz.myratingapp.match;

import com.netflix.dgs.codegen.generated.types.MatchInput;
import com.netflix.dgs.codegen.generated.types.MatchStatusEnum;
import com.turminaz.myratingapp.config.AuthenticationFacade;
import com.turminaz.myratingapp.model.Player;
import com.turminaz.myratingapp.player.PlayerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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

    @BeforeEach
    void setUp() {
        sut = new MatchService( repository, playerService, authenticationFacade, MatchMapper.INSTANCE);
    }

    @Test
    void createMatch() {
        //given

        MatchInput input = podamFactory.manufacturePojo(MatchInput.class);
        when(repository.save(any(Match.class))).thenAnswer(i -> Mono.just(i.getArguments()[0]));
        when(playerService.findById(anyString()))
                .thenAnswer(i -> Optional.of(new Player((String) i.getArguments()[0],"","" )));
        when(authenticationFacade.authenticatedUserId()).thenReturn(input.getTeam1().getMatchPlayer1());

        //when
        var result = sut.createMatch(input);

        //then
        assertThat(result.getId()).isNotBlank();
        assertThat(result.getStartTime()).isEqualTo(input.getStartTime());

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
        verify(playerService, times(4)).findById(anyString());
    }
}