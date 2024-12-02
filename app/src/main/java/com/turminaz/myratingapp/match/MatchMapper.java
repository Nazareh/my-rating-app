package com.turminaz.myratingapp.match;

import com.opencsv.bean.CsvToBeanBuilder;
import com.turminaz.myratingapp.model.Match;
import com.turminaz.myratingapp.model.MatchPlayer;
import com.turminaz.myratingapp.model.SetScore;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface MatchMapper {

    MatchMapper INSTANCE = Mappers.getMapper(MatchMapper.class);

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
                .setScores(dto.getScores().stream().map(
                        s -> new SetScore(s.team1, s.team2)
                ).toList());

    }

    default Match toMatch(PostMatchCsv dto) {
        List<SetScore> scores = new ArrayList<>();
        if(dto.getSet1Team1Score() + dto.getSet1Team2Score() > 0){
            scores.add(new SetScore(dto.getSet1Team1Score(), dto.getSet1Team2Score()));
        }

        if(dto.getSet2Team1Score() + dto.getSet2Team2Score() > 0){
            scores.add(new SetScore(dto.getSet2Team1Score(), dto.getSet2Team2Score()));
        }

        if(dto.getSet3Team1Score() + dto.getSet3Team2Score() > 0){
            scores.add(new SetScore(dto.getSet3Team1Score(), dto.getSet3Team2Score()));
        }

        return new Match()
                .setStartTime(dto.getStartTime().toInstant(ZoneOffset.UTC))
                .setPlayers(List.of(
                        new MatchPlayer().setId(dto.getTeam1Player1()).setTeam(Team.TEAM_1),
                        new MatchPlayer().setId(dto.getTeam1Player2()).setTeam(Team.TEAM_1),
                        new MatchPlayer().setId(dto.getTeam2Player1()).setTeam(Team.TEAM_2),
                        new MatchPlayer().setId(dto.getTeam2Player2()).setTeam(Team.TEAM_2)))
                .setScores(scores);

    }

    default Stream<Match> toMatchStream(InputStream inputStream) {
        return new CsvToBeanBuilder<PostMatchCsv>(new InputStreamReader(inputStream))
                .withType(PostMatchCsv.class)
                .build().parse().stream()
                .sorted(Comparator.comparing(PostMatchCsv::getStartTime))
                .map(this::toMatch);
    }
}
