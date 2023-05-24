package com.cyberethik.convocapi.persistance.dto;

import com.cyberethik.convocapi.persistance.entities.Convocations;
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
public class ReponseDto
{
    private Long id;
    private String choix;
    private boolean alerte;
    private String description;
    private Convocations convocation;
    private Date dateEnvoi;
    private Date createdAt;
}
