package com.turminaz.myratingapp.match;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
class MatchPlayer {
    private String id;
    private String name;
    private Team team;
    private MatchStatus status;
}
