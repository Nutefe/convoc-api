package com.cyberethik.convocapi.playload.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class FilterRequest {
  private List<String> noms;
  private List<Boolean> etats;

  private List<String> equipes;
  private List<String> membres;
  private List<Date> dateReponse;
  private List<String> reponses;
}
