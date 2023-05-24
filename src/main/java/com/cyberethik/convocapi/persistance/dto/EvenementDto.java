package com.cyberethik.convocapi.persistance.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.util.Date;

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
    private Date dateDebut;
    private Date dateFin;
    private Date createdAt;
}
