package com.cyberethik.convocapi.persistance.dto;

import com.cyberethik.convocapi.persistance.entities.Evenements;
import com.cyberethik.convocapi.persistance.entities.Membres;
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
public class ConvocationDto
{
    private Long id;
    private Date dateEnvoi;
    private Evenements evenement;
    private Membres membre;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createdAt;
}
