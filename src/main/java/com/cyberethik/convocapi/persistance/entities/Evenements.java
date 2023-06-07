package com.cyberethik.convocapi.persistance.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author nutefe
 * Model des comptes
 */
@Data
@Entity
@Table(name = "evenements")
@JsonIgnoreProperties(value = { "updatedAt", "deleted", "version" })
@EntityListeners({ AuditingEntityListener.class })
public class Evenements implements Serializable
{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(name = "libelle", nullable = false)
    private String libelle;
    @Column(name = "description")
    private String description;
    @Column(name = "dateDebut")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date dateDebut;
    @Column(name = "dateFin")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date dateFin;
    @Column(name = "envoyer", columnDefinition="tinyint(1) default 0")
    private boolean envoyer;
    @Column(name = "deleted", columnDefinition="tinyint(1) default 0")
    private boolean deleted;
    @Version
    @Basic(optional = false)
    @Column(nullable = false)
    private int version;
    @CreatedDate
    @Column(name = "created_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createdAt;
    @Column(name = "updated_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @LastModifiedDate
    private Date updatedAt;

    public Evenements() {
    }

    public Evenements(Long id) {
        this.id = id;
    }
}
