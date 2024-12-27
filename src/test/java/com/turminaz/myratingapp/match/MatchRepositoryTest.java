package com.turminaz.myratingapp.match;

import com.turminaz.myratingapp.model.Match;
import com.turminaz.myratingapp.model.MatchStatus;
import org.bson.types.ObjectId;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


@DataMongoTest
class MatchRepositoryTest {


    @Autowired
    MatchRepository matchRepository;

    PodamFactory podamFactory = new PodamFactoryImpl();

    @ParameterizedTest
    @EnumSource(MatchStatus.class)
    void findAllByStatusAndPlayersContainsShouldNotFindAny(MatchStatus status) {

        matchRepository.saveAll(List.of(
                podamFactory.manufacturePojo(Match.class)
                , podamFactory.manufacturePojo(Match.class)
                , podamFactory.manufacturePojo(Match.class)
                , podamFactory.manufacturePojo(Match.class)
                , podamFactory.manufacturePojo(Match.class)));


        assertThat(matchRepository.findAllByStatusAndPlayersIdIs(status,new ObjectId()))
                .isEmpty();

    }

    @ParameterizedTest
    @EnumSource(MatchStatus.class)
    void findAllByStatusAndPlayersContains(MatchStatus status) {

        var playerId =  new ObjectId();
        var matches = List.of(
                podamFactory.manufacturePojo(Match.class)
                , podamFactory.manufacturePojo(Match.class)
                , podamFactory.manufacturePojo(Match.class)
                , podamFactory.manufacturePojo(Match.class)
                , podamFactory.manufacturePojo(Match.class));

        matches.stream().limit(2).forEach(match -> {
            match.getPlayers().getFirst().setId(playerId.toString());
            match.setStatus(status);
        });
        matchRepository.saveAll(matches);

        assertThat(matchRepository.findAllByStatusAndPlayersIdIs(status, playerId))
                .hasSize(2);

    }
}