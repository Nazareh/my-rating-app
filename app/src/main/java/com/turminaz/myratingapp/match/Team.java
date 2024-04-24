package com.turminaz.myratingapp.match;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
class Team {
    private MatchPlayer matchPlayer1;
    private MatchPlayer matchPlayer2;
}