package com.turminaz.myratingapp.model;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.spring.data.firestore.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Player {
    @DocumentId
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