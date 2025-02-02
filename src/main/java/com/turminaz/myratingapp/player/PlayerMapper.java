package com.turminaz.myratingapp.player;

import com.google.firebase.auth.UserRecord;
import com.turminaz.myratingapp.dto.MatchDto;
import com.turminaz.myratingapp.model.Player;
import org.bson.types.ObjectId;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface PlayerMapper {
    PlayerMapper INSTANCE = Mappers.getMapper(PlayerMapper.class);

    default Player toPlayer(UserRecord userRecord){
        return new Player()
                .setUserUid(userRecord.getUid())
                .setName(userRecord.getDisplayName())
                .setEmail(userRecord.getEmail());
    }

    default Player toPlayer(RegisterPlayerDto registerDto) {
        return new Player()
                .setName(registerDto.getName())
                .setEmail(registerDto.getEmail());
    }
    PlayerDto toPlayerDto(Player registerDto);
    PlayerDto toPlayerDto(Player registerDto, List<MatchDto> pendingMatches);

  default  String objectIdTOString(ObjectId objectId){
      return objectId.toString();
  }


}
