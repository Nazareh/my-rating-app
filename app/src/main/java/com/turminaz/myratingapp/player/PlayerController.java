package com.turminaz.myratingapp.player;

import com.google.firebase.auth.FirebaseAuthException;
import com.turminaz.myratingapp.model.Player;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/player")
@RequiredArgsConstructor
class PlayerController {

    private final PlayerService service;

    @PostMapping("/register/csv")
    Set<PlayerDto> registerPlayersFromCsv(@RequestPart(value = "file") MultipartFile file, Principal principal) throws IOException, FirebaseAuthException {
        return service.registerPlayersFromCsv(file.getInputStream());
    }

    @GetMapping
    List<PlayerDto> getAllPlayers() {
        return service.getAllPlayers();
    }

}
