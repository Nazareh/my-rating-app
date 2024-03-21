package com.turminaz.myratingapp.player;

import com.google.firebase.auth.UserRecord;
import com.turminaz.myratingapp.model.Player;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface PlayerMapper {
    Player toPlayer(UserRecord userRecord);
}
