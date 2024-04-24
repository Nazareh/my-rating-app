package com.turminaz.myratingapp.match;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Disabled
class MatchRepositoryTest {

    @MockBean
    private JwtDecoder jwtDecoder;

    @Autowired
    private MatchRepository matchRepository;


    private final PodamFactory podamFactory = new PodamFactoryImpl();

    @Test
    void findByStartTime() {
        //given
        var matches = List.of(
                new Match().setStartTime(Instant.parse("2021-08-01T00:00:00Z")),
                new Match().setStartTime(Instant.parse("2021-08-02T00:00:00Z")),
                new Match().setStartTime(Instant.parse("2021-08-03T00:00:00Z"))

                );
        var result = matchRepository.saveAll(matches).blockLast();

        //when
//        var match = matchRepository.findByStartTime(Instant.parse("2021-08-02T00:00:00Z")).blockOptional().orElseThrow();
//
//        //then
//        assertThat(match).isEqualTo(matches.get(1));

    }
}