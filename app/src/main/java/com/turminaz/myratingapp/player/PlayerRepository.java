package com.turminaz.myratingapp.player;

import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;
import com.turminaz.myratingapp.model.Player;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


@Repository
interface PlayerRepository extends FirestoreReactiveRepository<Player> {

    Mono<Player> findByEmail(String email);
}
