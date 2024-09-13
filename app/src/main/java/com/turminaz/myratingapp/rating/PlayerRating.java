package com.turminaz.myratingapp.rating;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.spring.data.firestore.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class PlayerRating {

    @DocumentId
    private String id;

    private int matchesWon;
    private int matchesLost;
    private int gamesWon;
    private int gamesLost;
    private Map<String, List<Rating>> ratings = new HashMap<>();

    private Instant lastUpdated;
    private String lastMatchId;


}
