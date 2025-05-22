package com.cyberethik.convocapi.playload.request;

import com.cyberethik.convocapi.persistance.entities.Equipes;
import com.cyberethik.convocapi.persistance.entities.Membres;
import com.cyberethik.convocapi.persistance.entities.Organisations;
import com.cyberethik.convocapi.persistance.entities.Roles;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class EvenementRequest {
  private String libelle;
  private String description;
  private Membres coordinateur;
  @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
  private Date dateDebut;
  @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
  private Date dateFin;
  private List<Equipes> equipes = new ArrayList<>();
}
