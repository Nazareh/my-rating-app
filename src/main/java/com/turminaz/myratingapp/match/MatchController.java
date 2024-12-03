package com.turminaz.myratingapp.match;

import com.turminaz.myratingapp.config.IsAdmin;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/match")
@RequiredArgsConstructor
class MatchController {

    private final MatchService service;

    @PostMapping
    MatchDto postMatch(@RequestBody PostMatchDto matchDto)  {
        return service.postMatch(matchDto);
    }


    @PostMapping("/csv")
    @IsAdmin
    List<MatchDto> uploadMatchFromCsv(@RequestPart(value = "file") MultipartFile file) throws IOException {
        return service.uploadMatchFromCsv(file.getInputStream());
    }

    @GetMapping
    @IsAdmin
    List<MatchDto> getAllMatches() {
        return service.getAllMatches();
    }
}
