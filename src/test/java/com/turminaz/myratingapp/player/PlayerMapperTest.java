package com.turminaz.myratingapp.player;

import com.turminaz.myratingapp.model.Player;
import org.junit.jupiter.api.Test;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import static org.assertj.core.api.Assertions.assertThat;


class PlayerMapperTest {
    private PodamFactory podamFactory = new PodamFactoryImpl();

    @Test
    void toPlayer() {
        var dto = podamFactory.manufacturePojo(RegisterPlayerDto.class);

        var entity = PlayerMapper.INSTANCE.toPlayer(dto);

        assertThat(entity).usingRecursiveComparison()
                .ignoringFields("id", "gamesLost", "matchesWon", "matchesLost", "ratings", "lastRatings", "userUid", "gamesWon")
                .isEqualTo(dto);

        assertThat(entity.getId()).isNull();
        assertThat(entity.getGamesLost()).isEqualTo(0);
        assertThat(entity.getGamesWon()).isEqualTo(0);
        assertThat(entity.getMatchesLost()).isEqualTo(0);
        assertThat(entity.getMatchesWon()).isEqualTo(0);
        assertThat(entity.getUserUid()).isNull();
        assertThat(entity.getRatings()).isEmpty();
    }

    @Test
    void toPlayerDto() {
        var entity = podamFactory.manufacturePojo(Player.class);

        var dto = PlayerMapper.INSTANCE.toPlayerDto(entity);
        assertThat(dto).usingRecursiveComparison()
                .ignoringFields("userUid", "id")
                .isEqualTo(entity);

        assertThat(dto.id()).isEqualTo(entity.getId().toString());
    }
}