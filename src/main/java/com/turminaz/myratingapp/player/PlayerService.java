package com.turminaz.myratingapp.player;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.netflix.dgs.codegen.generated.types.PlayerResponse;
import com.turminaz.myratingapp.model.Player;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PlayerService {
    private final PlayerRepository repository;
    private final FirebaseAuth firebaseAuth;
    private final PlayerMapper mapper;

    public Optional<Player> findById(String id) {
        return repository.findById(id);
    }
    public PlayerResponse onboardPlayer(String id)  {
        try {
            return mapper.toPlayerResponse(
                    repository.save(
                            mapper.toPlayer(
                                    firebaseAuth.getUser(id))));

        } catch (FirebaseAuthException e) {
            throw new RuntimeException(e);
        }
    }
}
