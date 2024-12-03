package com.turminaz.myratingapp.match;

import com.opencsv.bean.CsvDate;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class PostMatchCsv {
   @CsvDate(value = "yyyy-MM-dd'T'HH:mm")
   private LocalDateTime startTime;
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