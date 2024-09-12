package com.turminaz.myratingapp.match;

import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;
import com.turminaz.myratingapp.model.Match;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.Instant;

@Repository
interface MatchRepository extends FirestoreReactiveRepository<Match> {
    Flux<Match> findAllByStartTimeGreaterThan(Instant instant);
}
