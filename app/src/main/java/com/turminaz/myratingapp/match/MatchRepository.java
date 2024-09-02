package com.turminaz.myratingapp.match;

import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Repository
interface MatchRepository extends FirestoreReactiveRepository<Match> {
    Flux<Match> findAllByStartTimeGreaterThan(Instant instant);
}
