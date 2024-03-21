package com.turminaz.myratingapp.player;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.turminaz.myratingapp.model.Player;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlayerService {
    private final PlayerRepository repository;
    private final FirebaseAuth firebaseAuth;
    private final PlayerMapper mapper;

    public List<Player> findAll() {
        return repository.findAll();
    }

    public Optional<Player> findById(String id) {
        return repository.findById(id);
    }

    public Player onboardPlayer(String id)  {
        try {
            return repository.save(mapper.toPlayer(getUserFromFirebase(id)));
        } catch (FirebaseAuthException e) {
            throw new RuntimeException(e);
        }
    }


    private UserRecord getUserFromFirebase(String id) throws FirebaseAuthException {
        return firebaseAuth.getUser(id);
    }
}
