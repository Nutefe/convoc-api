package com.cyberethik.convocapi.persistance.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
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
@Entity
@Table(name = "responsables")
@JsonIgnoreProperties(value = { "updatedAt", "deleted", "version" })
@EntityListeners({ AuditingEntityListener.class })
public class Responsables implements Serializable
{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(name = "email", unique = true)
    private String email;
    @Column(name = "libelle")
    private String libelle;
    @Column(name = "telephone")
    private String telephone;
    @Column(name = "adresse")
    private String adresse;
    @JoinColumn(name = "organisation", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Organisations organisation;
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

    public Responsables() {
    }

    public Responsables(Long id) {
        this.id = id;
    }
}
