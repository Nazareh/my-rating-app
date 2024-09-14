package com.turminaz.myratingapp.player;

import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;
import com.turminaz.myratingapp.model.Player;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface PlayerRepository extends FirestoreReactiveRepository<Player> {

    Mono<Player> findByEmail(String email);
}
