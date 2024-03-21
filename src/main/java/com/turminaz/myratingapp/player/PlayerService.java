package com.turminaz.myratingapp.player;

import com.google.cloud.firestore.Firestore;
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
    private final Firestore firestore;
    private final FirebaseAuth firebaseAuth;

    public Optional<Player> findById(String id) {
        return repository.findById(id).blockOptional();
    }
    public PlayerResponse onboardPlayer(String id) throws FirebaseAuthException {
            firestore.listCollections().forEach((c) -> System.out.println(c.getId()));
            return mapper.toPlayerResponse(
                    repository.save(
                            mapper.toPlayer(firebaseAuth.getUser(id))
                    ).block());
    }
}
