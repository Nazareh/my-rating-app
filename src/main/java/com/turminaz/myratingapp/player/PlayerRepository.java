package com.turminaz.myratingapp.player;

import com.turminaz.myratingapp.model.Player;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@Repository
class PlayerRepository {

    private final Set<Player> players = new HashSet<>();

    Optional<Player> findById(String id){
        return players.stream().filter(player -> player.id().equals(id)).findFirst();
    };

    Player save(Player player){
        players.add(player);
        return player;
    };
}
