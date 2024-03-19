package com.turminaz.myratingapp.match;

import java.io.Serializable;

record Team(MatchPlayer matchPlayer1, MatchPlayer matchPlayer2) implements Serializable {
}
