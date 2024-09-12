package com.turminaz.myratingapp.match;

import com.turminaz.myratingapp.model.Match;
import com.turminaz.myratingapp.model.MatchStatus;
import com.turminaz.myratingapp.model.Player;
import org.junit.jupiter.api.Test;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class MatchMapperTest {
    private final PodamFactory podamFactory = new PodamFactoryImpl();
//    @Test
//    void toMatchResponse() {
//        var match = podamFactory.manufacturePojo(Match.class);
//
//        var matchResponse = MatchMapper.INSTANCE.toMatchResponse(match);
//
//        assertThat(matchResponse).usingRecursiveComparison()
//                .ignoringFieldsOfTypes(OffsetDateTime.class)
//                .isEqualTo(match);
//
//        assertThat(matchResponse.getStartTime().toInstant()).isEqualTo(match.getStartTime());
//    }
//
//    @Test
//    void toMatchPlayer() {
//        var player = podamFactory.manufacturePojo(Player.class);
//        var matchStatus = podamFactory.manufacturePojo(MatchStatus.class);
//
//        var matchPlayer = MatchMapper.INSTANCE.toMatchPlayer(player, matchStatus);
//
//        assertThat(matchPlayer).usingRecursiveComparison()
//                .ignoringFieldsOfTypes(MatchStatus.class)
//                .isEqualTo(player);
//
//        assertThat(matchPlayer.getStatus()).isEqualTo(matchStatus);
//
//    }

//    @Test
//    void toMatch() {
//        var id = podamFactory.manufacturePojo(String.class);
//        var matchInput = podamFactory.manufacturePojo(MatchInput.class);
//        var matchPlayer1 = podamFactory.manufacturePojo(MatchPlayer.class);
//        var matchPlayer2 = podamFactory.manufacturePojo(MatchPlayer.class);
//        var matchPlayer3 = podamFactory.manufacturePojo(MatchPlayer.class);
//        var matchPlayer4 = podamFactory.manufacturePojo(MatchPlayer.class);
//
//        var match = MatchMapper.INSTANCE.toMatch(id, MatchStatus.PENDING, matchInput, matchPlayer1, matchPlayer2, matchPlayer3, matchPlayer4);
//
//        assertThat(match.getId()).isEqualTo(id);
//        assertThat(match.getStatus()).isEqualTo(MatchStatus.PENDING);
//        assertThat(match.getStartTime()).isEqualTo(matchInput.getStartTime().toInstant());
//        assertThat(match.getTeam1().getMatchPlayer1()).isEqualTo(matchPlayer1);
//        assertThat(match.getTeam1().getMatchPlayer2()).isEqualTo(matchPlayer2);
//        assertThat(match.getTeam2().getMatchPlayer1()).isEqualTo(matchPlayer3);
//        assertThat(match.getTeam2().getMatchPlayer2()).isEqualTo(matchPlayer4);
//        assertThat(match.getSetsPlayed()).usingRecursiveComparison().isEqualTo(matchInput.getSetsPlayed());
//
//    }
}