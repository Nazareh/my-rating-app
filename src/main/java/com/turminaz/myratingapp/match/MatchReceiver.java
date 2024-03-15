package com.turminaz.myratingapp.match;

import com.turminaz.myratingapp.match.domain.Match;
import org.springframework.stereotype.Component;

@Component
public class MatchReceiver {

    public void handleMessage(Match message) {
        System.out.println("Received <" + message.id() + ">");
    }
}
