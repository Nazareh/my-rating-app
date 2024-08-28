package com.turminaz.myratingapp.match;

import com.opencsv.bean.CsvDate;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class PostMatchDto {
   @CsvDate(value = "yyyy-MM-dd HH:mm")
   private LocalDate startTime;
   private String team1Player1;
   private String team1Player2;
   private String team2Player1;
   private String team2Player2;
   private int set1Team1Score;
   private int set1Team2Score;
   private int set2Team1Score;
   private int set2Team2Score;
   private int set3Team1Score;
   private int set3Team2Score;
}
