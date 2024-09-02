package com.turminaz.myratingapp.match;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.netflix.dgs.codegen.generated.types.MatchResponse;
import com.turminaz.myratingapp.player.PlayerDto;
import com.turminaz.myratingapp.player.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/match")
@RequiredArgsConstructor
class MatchController {

    private final MatchService service;
    private final FirebaseAuth firebaseAuth;

    @PostMapping("/csv")
    Set<MatchDto> registerPlayersFromCsv(@RequestPart(value = "file") MultipartFile file) throws IOException {
        return service.uploadMatchFromCsv(file.getInputStream());
    }


    @GetMapping
    List<MatchDto> getAllMatches() {
        return service.getAllMatches();
    }
}
