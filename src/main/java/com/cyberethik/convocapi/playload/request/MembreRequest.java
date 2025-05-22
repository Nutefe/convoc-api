package com.cyberethik.convocapi.playload.request;

import com.cyberethik.convocapi.persistance.entities.Accounts;
import com.cyberethik.convocapi.persistance.entities.Equipes;
import com.cyberethik.convocapi.persistance.entities.Responsables;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class MembreRequest {
  private String libelle;
  private String adresse;
  private String telephone;
  private String email;
  private boolean responsable;
  private boolean actif;
  @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
  private Date dateFin;
  private List<Equipes> equipes = new ArrayList<>();

  private String emailResponsable;
  private String libelleResponsable;
  private String telephoneResponsable;
  private String adresseResponsable;
}
