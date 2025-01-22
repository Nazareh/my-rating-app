package com.turminaz.myratingapp.match;

import com.turminaz.myratingapp.config.AuthenticationFacade;
import com.turminaz.myratingapp.model.MatchStatus;
import com.turminaz.myratingapp.model.Player;
import com.turminaz.myratingapp.player.PlayerRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;

import static com.turminaz.myratingapp.match.MatchTestUtils.p1t1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@Disabled
public class MatchServiceIT {

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Autowired
    MatchService matchService;

    @Autowired
    MatchRepository matchRepository;

    @Autowired
    PlayerRepository playerRepository;

    @MockitoBean
    private AuthenticationFacade authenticationFacade;


    @Test
    @DisplayName("Given a match is posted, when getting Players from the match, then the match is shown as PENDING")
    void shouldPopulatePlayersWithMatch() {
        //given
        when(authenticationFacade.getUserUid()).thenReturn(p1t1);
        when(authenticationFacade.isAdmin()).thenReturn(false);

        var postMatchDto = new PostMatchDto();
        postMatchDto.setStartTime(LocalDateTime.now().minusDays(1));
        postMatchDto.setScores(List.of(new SetScoreDto(5, 7), new SetScoreDto(5, 7)));
        postMatchDto.setTeam1Player1("p1t1@gmail.com");
        postMatchDto.setTeam1Player2("p1t2@gmail.com");
        postMatchDto.setTeam2Player1("p2t1@gmail.com");
        postMatchDto.setTeam2Player2("p2t2@gmail.com");

        //when
        matchService.postMatch(postMatchDto);

        //then
        var savedMatch = matchRepository.findAll().stream().findFirst().orElseThrow();

        assertThat(savedMatch.getStatus()).isEqualTo(MatchStatus.PENDING);

        for (Player player : playerRepository.findAll()) {
            assertThat(player.getPendingMatches()).contains(savedMatch);
        }

        assertThat(playerRepository.findAll()).hasSize(4)
                .flatExtracting(Player::getPendingMatches)
                .containsExactly(savedMatch,savedMatch,savedMatch,savedMatch);
    }

    @Test
    @DisplayName("Given a match is approved, when getting Players from the match, then the match does not show")
    void shouldRemoveMatchFromPopulatePlayers() {

    }
}
