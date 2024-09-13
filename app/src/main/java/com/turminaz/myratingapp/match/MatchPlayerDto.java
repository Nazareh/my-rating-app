package com.turminaz.myratingapp.match;

import com.turminaz.myratingapp.model.MatchStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
class MatchPlayerDto {
    private String id;
    private String name;
    private Team team;
    private MatchStatus status;
}