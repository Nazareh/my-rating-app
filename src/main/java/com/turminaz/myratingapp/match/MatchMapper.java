package com.turminaz.myratingapp.match;

import com.netflix.dgs.codegen.generated.types.MatchInput;
import com.netflix.dgs.codegen.generated.types.MatchResponse;
import com.turminaz.myratingapp.model.Player;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface MatchMapper {

    MatchMapper INSTANCE = Mappers.getMapper(MatchMapper.class);

    MatchResponse toMatchResponse(Match match);

//    @Mapping(source = "player.name", target = "name")
    MatchPlayer toMatchPlayer(Player player,MatchStatus status );

    default Match toMatch(String id, MatchInput input, MatchPlayer matchPlayer1, MatchPlayer matchPlayer2, MatchPlayer matchPlayer3, MatchPlayer matchPlayer4) {
        return new Match(id, input.getStartTime(),
                new Team(matchPlayer1, matchPlayer2),
                new Team(matchPlayer3, matchPlayer4),
                input.getSetsPlayed().stream().map(set -> new SetPlayed(set.getTeam1Score(), set.getTeam2Score())).toList());

    }
}
