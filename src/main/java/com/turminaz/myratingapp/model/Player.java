package com.turminaz.myratingapp.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

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
    private String id;
    private String userUid;
    private String name;
    private String email;

    private int matchesWon;
    private int matchesLost;
    private int gamesWon;
    private int gamesLost;
    private Map<String, List<Rating>> ratings = new HashMap<>();

}