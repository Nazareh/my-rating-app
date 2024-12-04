package com.turminaz.myratingapp.match;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SetScoreDto {
    private int team1;
    private int team2;
}
