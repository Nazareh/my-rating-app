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

    default Player toPlayer(UserRecord userRecord){
        return new Player()
                .setUserUid(userRecord.getUid())
                .setName(userRecord.getDisplayName())
                .setEmail(userRecord.getEmail());
    };
    PlayerResponse toPlayerResponse(Player player);

    default Player toPlayer(RegisterPlayerDto registerDto) {
        return new Player()
                .setName(registerDto.getName())
                .setEmail(registerDto.getEmail());
    }
    PlayerDto toPlayerDto(Player registerDto);
}
