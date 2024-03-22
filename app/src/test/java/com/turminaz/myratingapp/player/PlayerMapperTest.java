package com.turminaz.myratingapp.player;

import com.turminaz.myratingapp.model.Player;
import org.junit.jupiter.api.Test;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import static org.assertj.core.api.Assertions.assertThat;


class PlayerMapperTest {
    private PodamFactory podamFactory = new PodamFactoryImpl();

    @Test
    void toPlayerResponse() {
        var player = podamFactory.manufacturePojo(Player.class);
        var playerResponse = PlayerMapper.INSTANCE.toPlayerResponse(player);
        assertThat(playerResponse).usingRecursiveComparison().isEqualTo(player);
    }
}