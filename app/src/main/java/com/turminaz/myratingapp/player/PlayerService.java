package com.turminaz.myratingapp.player;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.netflix.dgs.codegen.generated.types.PlayerResponse;
import com.opencsv.bean.CsvToBeanBuilder;
import com.turminaz.myratingapp.model.MatchStatus;
import com.turminaz.myratingapp.model.Player;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class PlayerService {
    private final PlayerRepository repository;
    private final PlayerMapper mapper;
    private final FirebaseAuth firebaseAuth;
    private final JmsTemplate jmsTemplate;

    public Optional<Player> findById(String id) {
        return repository.findById(id).blockOptional();
    }

    public Player createPlayer(String id) {
        try {
            return repository.save(mapper.toPlayer(firebaseAuth.getUser(id))).block();
        } catch (FirebaseAuthException e) {
            throw new RuntimeException(e);
        }
    }

    PlayerResponse onboardPlayer(String id) {
        return mapper.toPlayerResponse(createPlayer(id));
    }

    Set<PlayerDto> registerPlayersFromCsv(InputStream inputStream) {

        return new CsvToBeanBuilder<RegisterPlayerDto>(new InputStreamReader(inputStream))
                .withType(RegisterPlayerDto.class)
                .build().parse().stream()
                .map(mapper::toPlayer)
                .map(p -> repository.findByEmail(p.getEmail()).blockOptional()
                        .orElseGet(() -> repository.save(p).block()))
                .filter(Objects::nonNull)
                .peek(player -> jmsTemplate.convertAndSend("playerCreated", player))
                .map(mapper::toPlayerDto)
                .collect(Collectors.toSet());
    }

    List<PlayerDto> getAllPlayers() {
        return repository.findAll().collectList().block().stream().map(mapper::toPlayerDto).collect(Collectors.toList());
    }
}
