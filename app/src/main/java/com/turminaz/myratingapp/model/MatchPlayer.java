package com.turminaz.myratingapp.model;

import com.turminaz.myratingapp.match.Team;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class MatchPlayer {
    private String id;
    private String name;
    private Team team;
    private MatchStatus status;
}
