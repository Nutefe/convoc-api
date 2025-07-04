package com.cyberethik.convocapi.playload.request;

import com.cyberethik.convocapi.persistance.entities.Membres;
import com.cyberethik.convocapi.persistance.entities.Organisations;
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
import java.util.List;

/**
 *
 * @author nutefe
 * Model des comptes
 */
@Data
@NoArgsConstructor
public class EquipeRequest {
    private Long id;
    private String libelle;
    private String description;
    private Organisations organisation;
    private boolean actif;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date dateFin;
    List<Membres> membres;
}
