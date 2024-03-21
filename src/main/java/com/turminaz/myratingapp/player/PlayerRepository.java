package com.turminaz.myratingapp.player;

import com.turminaz.myratingapp.model.Player;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
class PlayerRepository {
    List<Player> findAll(){
        return List.of();
    };

    Optional<Player> findById(String id){
        return Optional.empty();
    };

    Player save(Player player){
        return player;
    };
}
