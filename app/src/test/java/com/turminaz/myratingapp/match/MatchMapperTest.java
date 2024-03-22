package com.turminaz.myratingapp.match;

import com.netflix.dgs.codegen.generated.types.MatchInput;
import com.turminaz.myratingapp.model.Player;
import org.junit.jupiter.api.Test;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import static org.assertj.core.api.Assertions.assertThat;

class MatchMapperTest {
    private final PodamFactory podamFactory = new PodamFactoryImpl();
    @Test
    void toMatchResponse() {
        var match = podamFactory.manufacturePojo(Match.class);

        var matchResponse = MatchMapper.INSTANCE.toMatchResponse(match);

        assertThat(matchResponse).usingRecursiveComparison().isEqualTo(match);
    }

    @Test
    void toMatchPlayer() {
        var player = podamFactory.manufacturePojo(Player.class);
        var matchStatus = podamFactory.manufacturePojo(MatchStatus.class);

        var matchPlayer = MatchMapper.INSTANCE.toMatchPlayer(player, matchStatus);

        assertThat(matchPlayer).usingRecursiveComparison()
                .ignoringFieldsOfTypes(MatchStatus.class)
                .isEqualTo(player);

        assertThat(matchPlayer.status()).isEqualTo(matchStatus);

    }

    @Test
    void toMatch() {
        var id = podamFactory.manufacturePojo(String.class);
        var matchInput = podamFactory.manufacturePojo(MatchInput.class);
        var matchPlayer1 = podamFactory.manufacturePojo(MatchPlayer.class);
        var matchPlayer2 = podamFactory.manufacturePojo(MatchPlayer.class);
        var matchPlayer3 = podamFactory.manufacturePojo(MatchPlayer.class);
        var matchPlayer4 = podamFactory.manufacturePojo(MatchPlayer.class);

        var match = MatchMapper.INSTANCE.toMatch(id, matchInput, matchPlayer1, matchPlayer2, matchPlayer3, matchPlayer4);

        assertThat(match.id()).isEqualTo(id);
        assertThat(match.startTime()).isEqualTo(matchInput.getStartTime());
        assertThat(match.team1().matchPlayer1()).isEqualTo(matchPlayer1);
        assertThat(match.team1().matchPlayer2()).isEqualTo(matchPlayer2);
        assertThat(match.team2().matchPlayer1()).isEqualTo(matchPlayer3);
        assertThat(match.team2().matchPlayer2()).isEqualTo(matchPlayer4);
        assertThat(match.setsPlayed()).usingRecursiveComparison().isEqualTo(matchInput.getSetsPlayed());

    }
}