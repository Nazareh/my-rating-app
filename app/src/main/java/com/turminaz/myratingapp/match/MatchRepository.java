package com.turminaz.myratingapp.match;

import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;
import reactor.core.publisher.Flux;

import java.time.Instant;

interface MatchRepository extends FirestoreReactiveRepository<Match> {
    Flux<Match> findAllByStartTime(Instant parse);
}
