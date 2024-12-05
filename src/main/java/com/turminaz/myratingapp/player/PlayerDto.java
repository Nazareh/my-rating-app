package com.turminaz.myratingapp.player;

import com.turminaz.myratingapp.model.Rating;
import com.turminaz.myratingapp.model.RatingType;

import java.util.List;
import java.util.Map;

public record PlayerDto(String id, String name, int matchesWon, int matchesLost, int gamesWon, int gamesLost, Map<RatingType, List<Rating>> ratings, Map<RatingType, Rating> lastRatings) { }
