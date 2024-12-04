package com.turminaz.myratingapp.player;

import com.turminaz.myratingapp.model.Rating;

import java.util.List;
import java.util.Map;

public record PlayerDto(String id, String name, int matchesWon, int matchesLost, int gamesWon, int gamesLost, Map<String, List<Rating>> ratings) { }