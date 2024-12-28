package com.turminaz.myratingapp.match;

import com.turminaz.myratingapp.config.IsAdmin;
import com.turminaz.myratingapp.model.MatchStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

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
    List<MatchDto> getAllMatches(@RequestParam Optional<MatchStatus> status) {
        return service.getMatches(status);
    }

    @PostMapping("{matchId}/approve")
    MatchDto approveMatch(@PathVariable String matchId)  {
        return service.approve(matchId);
    }

    @PostMapping("{matchId}/reject")
    MatchDto rejectMatch(@PathVariable String matchId)  {
        return service.reject(matchId);
    }
}
