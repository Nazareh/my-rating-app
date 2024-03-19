package com.turminaz.myratingapp.match;

import com.netflix.dgs.codegen.generated.types.MatchInput;
import com.netflix.dgs.codegen.generated.types.MatchResponse;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface MatchMapper {

    MatchMapper INSTANCE = Mappers.getMapper(MatchMapper.class);


    MatchResponse toMatchResponse(Match match);

    default com.netflix.dgs.codegen.generated.types.MatchPlayer map(String value){
        return com.netflix.dgs.codegen.generated.types.MatchPlayer.newBuilder().id(value)
                .build();
    }

    default Match toMatch(String id, MatchInput input, MatchPlayer matchPlayer1, MatchPlayer matchPlayer2, MatchPlayer matchPlayer3, MatchPlayer matchPlayer4) {
        return new Match(id, input.getStartTime(),
                new Team(matchPlayer1, matchPlayer2),
                new Team(matchPlayer3, matchPlayer4),
                input.getSetsPlayed().stream().map(set -> new SetPlayed(set.getTeam1Score(), set.getTeam2Score())).toList());

    }
}
