package com.turminaz.myratingapp.player;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.netflix.dgs.codegen.generated.types.PlayerResponse;
import com.turminaz.myratingapp.model.Player;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlayerService {
    private final PlayerRepository repository;
    private final PlayerMapper mapper;
    private final FirebaseAuth firebaseAuth;

    public Optional<Player> findById(String id) {
        return repository.findById(id).blockOptional();
    }
    public PlayerResponse onboardPlayer(String id) {
            return mapper.toPlayerResponse(createPlayer(id));
    }

    public Player createPlayer(String id)  {
        try {
            return repository.save(mapper.toPlayer(firebaseAuth.getUser(id))).block();
        } catch (FirebaseAuthException e) {
            throw new RuntimeException(e);
        }
    }
}
