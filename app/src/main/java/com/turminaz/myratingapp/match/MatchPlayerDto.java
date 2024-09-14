package com.turminaz.myratingapp.match;

import com.turminaz.myratingapp.model.MatchStatus;
import com.turminaz.myratingapp.model.Rating;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
class MatchPlayerDto {
    private String id;
    private String name;
    private Team team;
    private Map<String, Rating> ratings = new HashMap<>();
    private MatchStatus status;
}
