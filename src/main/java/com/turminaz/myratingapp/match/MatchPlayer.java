package com.turminaz.myratingapp.match;

import java.io.Serializable;

record MatchPlayer(String id, String name, MatchStatus status) implements Serializable {
}
