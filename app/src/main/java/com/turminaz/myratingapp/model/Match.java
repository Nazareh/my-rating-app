package com.turminaz.myratingapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.Instant;
import java.util.List;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Match {
    @MongoId
    private String id;
    private Instant startTime;
    private List<MatchPlayer> players;
    private int set1Team1Score;
    private int set1Team2Score;
    private int set2Team1Score;
    private int set2Team2Score;
    private int set3Team1Score;
    private int set3Team2Score;
    private MatchStatus status;
    private String rejectedReason;
}
