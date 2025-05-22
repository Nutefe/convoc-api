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
@Table(name = "organisations")
@JsonIgnoreProperties(value = { "updatedAt", "deleted", "version" })
@EntityListeners({ AuditingEntityListener.class })
public class Organisations implements Serializable
{
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    @Column(name = "nom", nullable = false)
    private String nom;
    @Column(name = "desciption")
    private String desciption;
    @Column(name = "devise")
    private String devise;
    @Column(name = "equipeMax")
    private Long equipeMax = 2L;
    @Column(name = "evenementActifs")
    private Long evenementActifs = 2L;
    @Column(name = "membreEquActifs")
    private Long membreEquActifs = 25L;
    @Column(name = "membreEventMax")
    private Long membreEventMax = 50L;
    @Column(name = "membreActifs")
    private Long membreActifs = 50L;
    @Column(name = "convocMax")
    private Long convocMax = 2L;
    @Column(name = "logo", columnDefinition = "varchar(255) default ''")
    private String logo;
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

    public Organisations() {
    }

    public Organisations(Long id) {
        this.id = id;
    }
}
