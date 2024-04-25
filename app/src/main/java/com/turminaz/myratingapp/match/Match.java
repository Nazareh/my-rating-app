package com.turminaz.myratingapp.match;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.spring.data.firestore.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
class Match {
    @DocumentId
    private String id;
    private Instant startTime;
    private Team team1;
    private Team team2;
    private List<SetPlayed> setsPlayed;
    private MatchStatus status;
}
