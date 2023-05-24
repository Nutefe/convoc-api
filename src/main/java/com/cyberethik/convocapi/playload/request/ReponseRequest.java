package com.cyberethik.convocapi.playload.request;

import com.cyberethik.convocapi.persistance.entities.Convocations;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author nutefe
 * Model des comptes
 */
@Data
@NoArgsConstructor
public class ReponseRequest {
    private Long id;
    private String choix;
    private boolean alerte;
    private String description;
    private Convocations convocation;
    private Date dateEnvoi;
}
