package com.cyberethik.convocapi.persistance.dto;

import com.cyberethik.convocapi.persistance.entities.Accounts;
import com.cyberethik.convocapi.persistance.entities.Equipes;
import com.cyberethik.convocapi.persistance.entities.Organisations;
import com.cyberethik.convocapi.persistance.entities.Responsables;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author nutefe
 * Model des comptes
 */
@Data
public class MembreDto
{
    private Long id;
    private String libelle;
    private String adresse;
    private Accounts account;
    private Responsables responsable;
    private List<Equipes> equipes = new ArrayList<>();
    private boolean actif;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date dateFin;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createdAt;
}
