package com.turminaz.myratingapp.rating;

import com.turminaz.myratingapp.match.MatchService;
import com.turminaz.myratingapp.player.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RatingService {
    private final PlayerService playerService;
    private final MatchService matchService;

    public void recalculateRatings() {
        playerService.eraseAllRatings();
        matchService.republishedApprovedMatches();
    }
}
