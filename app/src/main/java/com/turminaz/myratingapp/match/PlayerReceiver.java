package com.turminaz.myratingapp.match;

import com.turminaz.myratingapp.events.PlayerEvent;
import org.springframework.stereotype.Component;

@Component
class PlayerReceiver {

    void handleMessage(PlayerEvent message) {
        System.out.println("Received <" + message.id() + ">");
    }
}
