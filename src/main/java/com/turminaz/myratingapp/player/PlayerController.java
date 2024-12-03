package com.turminaz.myratingapp.player;

import com.google.firebase.auth.FirebaseAuthException;
import com.netflix.dgs.codegen.generated.types.PlayerResponse;
import com.turminaz.myratingapp.config.IsAdmin;
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
    @IsAdmin
    Set<PlayerDto> registerPlayersFromCsv(@RequestPart(value = "file") MultipartFile file) throws IOException {
        return service.registerPlayersFromCsv(file.getInputStream());
    }

    @PostMapping("onboard-myself")
    PlayerResponse onboardMyself(Principal principal) throws FirebaseAuthException {
        return service.onboardPlayer(principal.getName());
    }

    @GetMapping
    List<PlayerDto> getAllPlayers() {
        return service.getAllPlayers();
    }


    @GetMapping("/{id}")
    PlayerDto getPlayer(@PathVariable String id) {
        return service.getPlayerById(id);
    }

}
