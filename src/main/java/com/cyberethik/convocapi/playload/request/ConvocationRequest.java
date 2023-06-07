package com.cyberethik.convocapi.playload.request;

import com.cyberethik.convocapi.persistance.entities.Equipes;
import com.cyberethik.convocapi.persistance.entities.Evenements;
import com.cyberethik.convocapi.persistance.entities.Membres;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class ConvocationRequest {
  private Date dateEnvoi;
  private Evenements evenement;
  private List<Equipes> equipes;
  private List<Membres> membres = new ArrayList<>();
}
