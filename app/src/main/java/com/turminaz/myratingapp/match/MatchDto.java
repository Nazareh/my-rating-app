package com.turminaz.myratingapp.match;

import com.turminaz.myratingapp.model.MatchStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
class MatchDto {
    private String id;
    private LocalDateTime startTime;
    private Set<MatchPlayerDto> players;
    private int set1Team1Score;
    private int set1Team2Score;
    private int set2Team1Score;
    private int set2Team2Score;
    private int set3Team1Score;
    private int set3Team2Score;
    private MatchStatus status;
    private String rejectedReason;
}
