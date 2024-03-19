package com.turminaz.myratingapp.match;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

record Match(String id, LocalDateTime startTime, Team team1, Team team2, List<SetPlayed> setsPlayed) implements Serializable {
}
