package com.turminaz.myratingapp.rating;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService service;

    @PostMapping("/recalculate")
    void recalculateRatings(){
        service.recalculateRatings();
    }
}
