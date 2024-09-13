package com.turminaz.myratingapp.rating;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.HashMap;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rating {
    private String type;
    private Instant dateTime;
    private String value;
}