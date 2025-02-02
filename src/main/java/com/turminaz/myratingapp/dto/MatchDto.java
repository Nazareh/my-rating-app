package com.turminaz.myratingapp.dto;

import com.turminaz.myratingapp.match.SetScoreDto;
import com.turminaz.myratingapp.model.MatchStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class MatchDto {
    private String id;
    private LocalDateTime startTime;
    private Set<MatchPlayerDto> players;
    private List<SetScoreDto> scores;
    private MatchStatus status;
    private String reason;

}
