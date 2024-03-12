package com.turminaz.myratingapp.Match;

import com.netflix.dgs.codegen.generated.types.Match;
import com.netflix.dgs.codegen.generated.types.MatchInput;
import com.netflix.dgs.codegen.generated.types.MatchPlayer;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface MatchMapper {

    @Mapping(target = "id", ignore = true)
    Match toMatch(MatchInput input);

    default MatchPlayer map(String value){
        return MatchPlayer.newBuilder().id(value)
                .build();
    }
}
