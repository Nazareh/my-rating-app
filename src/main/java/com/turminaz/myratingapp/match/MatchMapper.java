package com.turminaz.myratingapp.match;

import com.netflix.dgs.codegen.generated.types.MatchInput;
import com.netflix.dgs.codegen.generated.types.MatchPlayer;
import com.netflix.dgs.codegen.generated.types.MatchResponse;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface MatchMapper {

    @Mapping(target = "id", ignore = true)
    MatchResponse toMatch(MatchInput input);

    default MatchPlayer map(String value){
        return MatchPlayer.newBuilder().id(value)
                .build();
    }
}
