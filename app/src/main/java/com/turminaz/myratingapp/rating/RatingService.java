package com.turminaz.myratingapp.rating;

import com.turminaz.myratingapp.model.Match;
import com.turminaz.myratingapp.player.PlayerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.jms.core.JmsTemplate;


@Log4j2
@RequiredArgsConstructor
public abstract class RatingService {

    public final RatingType ratingType;
    public final JmsTemplate jmsTemplate;
    protected final PlayerRepository repository;

    protected abstract void calculateRating(Match match);


}
