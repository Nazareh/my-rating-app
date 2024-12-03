package com.turminaz.myratingapp.player;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RegisterPlayerDto{
   private String email;
   private String name;
}
