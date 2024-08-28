package com.turminaz.myratingapp.player;

import com.google.firebase.auth.UserRecord;
import com.netflix.dgs.codegen.generated.types.PlayerResponse;
import com.turminaz.myratingapp.model.Player;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface PlayerMapper {
    PlayerMapper INSTANCE = Mappers.getMapper(PlayerMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userUid", source = "uid")
    @Mapping(target = "name", source = "displayName")
    Player toPlayer(UserRecord userRecord);
    PlayerResponse toPlayerResponse(Player player);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userUid", ignore = true)
    Player toPlayer(RegisterPlayerDto registerDto);
    PlayerDto toPlayerDto(Player registerDto);
}
