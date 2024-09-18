package com.turminaz.myratingapp.player;

import com.turminaz.myratingapp.config.IsAdmin;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    @GetMapping
    List<PlayerDto> getAllPlayers() {
        return service.getAllPlayers();
    }

}
