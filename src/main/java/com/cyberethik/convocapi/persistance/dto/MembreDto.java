package com.cyberethik.convocapi.persistance.dto;

import com.cyberethik.convocapi.persistance.entities.Accounts;
import com.cyberethik.convocapi.persistance.entities.Responsables;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.util.Date;

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
    private boolean actif;
    private Date dateFin;
    private Date createdAt;
}
