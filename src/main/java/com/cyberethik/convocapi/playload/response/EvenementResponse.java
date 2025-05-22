package com.cyberethik.convocapi.playload.response;

import com.cyberethik.convocapi.persistance.entities.Equipes;
import com.cyberethik.convocapi.persistance.entities.Evenements;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class EvenementResponse {
  private Evenements evenement;
  private Date dateEnvoie;
  private Long nbrPersConvoc = 0L;// nombre de personne convoquer
  private Long nbrReponseRecu  = 0L;// nombre de reponse recu
  private String pourcReponseRecu = "0.0";// pourcentage de reponse recu
  private Long nbrReponsePositif = 0L;// nombre de reponse Positif
  private String pourcReponsePositif = "0.0";// pourcentage de reponse Positif
  private Long nbrReponseNegatif = 0L;// nombre de reponse Negatif
  private String pourcReponseNegatif = "0.0";// pourcentage de reponse Negatif
  private Long nbrReponseNeant = 0L;// nombre de reponse Negatif
  private String pourcReponseNeant = "0.0";// pourcentage de reponse Negatif
}
