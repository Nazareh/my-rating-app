package com.turminaz.myratingapp.match;

import com.turminaz.myratingapp.model.Match;
import com.turminaz.myratingapp.model.MatchStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
interface MatchRepository extends MongoRepository<Match, String> {
    List<Match> findAllByStartTimeGreaterThan(Instant instant);
    List<Match> findAllByStatus(MatchStatus status);
}
