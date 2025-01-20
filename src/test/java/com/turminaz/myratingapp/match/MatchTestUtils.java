package com.turminaz.myratingapp.match;

import com.turminaz.myratingapp.model.Match;
import com.turminaz.myratingapp.model.MatchPlayer;
import com.turminaz.myratingapp.model.MatchStatus;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.util.List;

class MatchTestUtils {

   static final String p1t1 = new ObjectId().toString();
   static final String p1t2 = new ObjectId().toString();
   static final String p2t1 = new ObjectId().toString();
   static final String p2t2 = new ObjectId().toString();

   static Match createMatch() {
        return new Match()
                .setId(new ObjectId().toString())
                .setStartTime(Instant.now())
                .setStatus(MatchStatus.PENDING)
                .setPlayers(List.of(
                        new MatchPlayer().setId(p1t1).setStatus(MatchStatus.APPROVED).setTeam(Team.TEAM_1),
                        new MatchPlayer().setId(p2t1).setStatus(MatchStatus.PENDING).setTeam(Team.TEAM_1),
                        new MatchPlayer().setId(p1t2).setStatus(MatchStatus.PENDING).setTeam(Team.TEAM_2),
                        new MatchPlayer().setId(p2t2).setStatus(MatchStatus.PENDING).setTeam(Team.TEAM_2)
                ));
    }
}
