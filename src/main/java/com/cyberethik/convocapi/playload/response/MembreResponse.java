package com.cyberethik.convocapi.playload.response;

import com.cyberethik.convocapi.persistance.entities.Equipes;
import com.cyberethik.convocapi.persistance.entities.Evenements;
import com.cyberethik.convocapi.persistance.entities.Membres;
import com.cyberethik.convocapi.persistance.entities.Reponses;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.coyote.Response;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class MembreResponse {
  private Membres membre;
  private List<Equipes> equipes;
  private Reponses response;
}
