package com.turminaz.myratingapp.dto;

import com.turminaz.myratingapp.match.Team;
import com.turminaz.myratingapp.model.MatchStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchPlayerDto {
    private String id;
    private String name;
    private Team team;
    private MatchStatus status;
}
