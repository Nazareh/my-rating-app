package com.turminaz.myratingapp.player;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping("/api/v1/player")
@RequiredArgsConstructor
class PlayerController {

    private final PlayerService service;
    private final FirebaseAuth firebaseAuth;

    @PostMapping("/register/csv")
    Set<PlayerDto> registerPlayersFromCsv(@RequestPart(value = "file") MultipartFile file, Principal principal) throws IOException, FirebaseAuthException {
        return service.registerPlayersFromCsv(file.getInputStream());
    }

    @GetMapping
    List<PlayerDto> getAllPlayers() {
        return service.getAllPlayers();
    }
}
