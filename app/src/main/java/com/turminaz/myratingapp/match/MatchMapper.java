package com.turminaz.myratingapp.match;

import com.opencsv.bean.CsvToBeanBuilder;
import com.turminaz.myratingapp.model.Match;
import com.turminaz.myratingapp.model.MatchPlayer;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.*;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface MatchMapper {

    MatchMapper INSTANCE = Mappers.getMapper(MatchMapper.class);
//
//    MatchResponse toMatchResponse(Match match);

//    MatchPlayer toMatchPlayer(Player player, MatchStatus status );

    MatchDto toMatchDto(Match document);

    default LocalDateTime toOffsetDateTime(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    }


    default Match toMatch(PostMatchDto dto) {
        return new Match()
                .setStartTime(dto.getStartTime().toInstant(ZoneOffset.UTC))
                .setPlayers(List.of(
                        new MatchPlayer().setId(dto.getTeam1Player1()).setTeam(Team.TEAM_1),
                        new MatchPlayer().setId(dto.getTeam1Player2()).setTeam(Team.TEAM_1),
                        new MatchPlayer().setId(dto.getTeam2Player1()).setTeam(Team.TEAM_2),
                        new MatchPlayer().setId(dto.getTeam2Player2()).setTeam(Team.TEAM_2)))
                .setSet1Team1Score(dto.getSet1Team1Score())
                .setSet1Team2Score(dto.getSet1Team2Score())
                .setSet2Team1Score(dto.getSet2Team1Score())
                .setSet2Team2Score(dto.getSet2Team2Score())
                .setSet3Team1Score(dto.getSet3Team1Score())
                .setSet3Team2Score(dto.getSet3Team2Score());
    }

    default Stream<Match> toMatchStream(InputStream inputStream) {
        return new CsvToBeanBuilder<PostMatchDto>(new InputStreamReader(inputStream))
                .withType(PostMatchDto.class)
                .build().parse().stream()
                .sorted(Comparator.comparing(PostMatchDto::getStartTime))
                .map(this::toMatch);
    }
}
