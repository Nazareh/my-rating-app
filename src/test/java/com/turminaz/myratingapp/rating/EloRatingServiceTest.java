package com.turminaz.myratingapp.rating;

import com.turminaz.myratingapp.match.Team;
import com.turminaz.myratingapp.model.*;
import com.turminaz.myratingapp.player.PlayerRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Disabled
class EloRatingServiceTest {

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Autowired
    PlayerRepository repository;

    @Autowired
    EloRatingService sut;

    @Test
    void name() {
        //given
        var p1 = new Player();
        var p2 = new Player();
        var p3 = new Player();
        var p4 = new Player();

        var players = List.of(p1, p2, p3, p4);
        repository.saveAll(players);

        var match = new Match()
                .setStartTime(Instant.now())
                .setPlayers(List.of(
                        new MatchPlayer(p1.getId().toString(), p1.getName(), Team.TEAM_1, MatchStatus.PENDING),
                        new MatchPlayer(p2.getId().toString(), p2.getName(), Team.TEAM_1, MatchStatus.PENDING),
                        new MatchPlayer(p3.getId().toString(), p3.getName(), Team.TEAM_2, MatchStatus.PENDING),
                        new MatchPlayer(p4.getId().toString(), p4.getName(), Team.TEAM_2, MatchStatus.PENDING)
                ))
                .setScores(List.of(new SetScore(6, 0), new SetScore(6, 0)));

        //when
        sut.calculateRating(match);

        //then
        assertThat(repository.findById(p1.getId()).orElseThrow().getRatings().get(RatingType.ELO)).hasSize(1);
        assertThat(repository.findById(p1.getId()).orElseThrow().getLastRatings().get(RatingType.ELO).getValue()).isEqualTo(1520);

        assertThat(repository.findById(p2.getId()).orElseThrow().getRatings().get(RatingType.ELO)).hasSize(1);
        assertThat(repository.findById(p2.getId()).orElseThrow().getLastRatings().get(RatingType.ELO).getValue()).isEqualTo(1520);

        assertThat(repository.findById(p3.getId()).orElseThrow().getRatings().get(RatingType.ELO)).hasSize(1);
        assertThat(repository.findById(p3.getId()).orElseThrow().getLastRatings().get(RatingType.ELO).getValue()).isEqualTo(1480);

        assertThat(repository.findById(p4.getId()).orElseThrow().getRatings().get(RatingType.ELO)).hasSize(1);
        assertThat(repository.findById(p4.getId()).orElseThrow().getLastRatings().get(RatingType.ELO).getValue()).isEqualTo(1480);


    }
}