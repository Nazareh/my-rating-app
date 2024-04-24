package com.turminaz.myratingapp.match;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
class MatchPlayer {
    private String id;
    private String name;
    private MatchStatus status;
}
