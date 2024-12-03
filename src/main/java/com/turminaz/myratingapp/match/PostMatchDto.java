package com.turminaz.myratingapp.match;

import com.opencsv.bean.CsvDate;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class PostMatchDto {
   private LocalDateTime startTime;
   private String team1Player1;
   private String team1Player2;
   private String team2Player1;
   private String team2Player2;
   private List<SetScoreDto> scores;

}