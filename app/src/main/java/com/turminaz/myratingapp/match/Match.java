package com.turminaz.myratingapp.match;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.spring.data.firestore.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
class Match {
    @DocumentId
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
