package com.turminaz.myratingapp.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Player {

    @MongoId
    private ObjectId id;
    private String userUid;
    private String name;
    private String email;

    private int matchesWon;
    private int matchesLost;
    private int gamesWon;
    private int gamesLost;
    private Map<RatingType, List<Rating>> ratings = new HashMap<>();
    private Map<RatingType, Rating> lastRatings = new HashMap<>();

}