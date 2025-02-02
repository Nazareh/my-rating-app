package com.turminaz.myratingapp.playerMatchService;

import com.turminaz.myratingapp.dto.MatchDto;
import com.turminaz.myratingapp.match.MatchMapper;
import com.turminaz.myratingapp.match.MatchRepository;
import com.turminaz.myratingapp.model.MatchStatus;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlayerMatchService {
    private final MatchMapper matchMapper;
    private final MatchRepository matchRepository;

    public List<MatchDto> getMatchesByPlayer(ObjectId playerId, Optional<MatchStatus> status)  {

        var matches = matchRepository
                .findAllByStatusAndPlayersIdIs(
                        status.orElse(MatchStatus.PENDING),
                        playerId);

        return matches.stream()
                .map(matchMapper::toMatchDto)
                .collect(Collectors.toList());
    }

}
