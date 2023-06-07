package com.cyberethik.convocapi.persistance.dto;

import com.cyberethik.convocapi.persistance.entities.Equipes;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
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
public class EvenementDto
{
    private Long id;
    private String libelle;
    private String description;
    private List<Equipes> equipes = new ArrayList<>();
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date dateDebut;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date dateFin;
    private boolean envoyer;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createdAt;
}
