// 
// Decompiled by Procyon v0.5.36
// 

package com.cyberethik.convocapi.persistance.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data

@Entity
@Table(name = "accountOrganisations")
@JsonIgnoreProperties(value = { "updatedAt", "deleted", "version" })
@EntityListeners({ AuditingEntityListener.class })
public class AccountOrganisations implements Serializable
{
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected AccountOrganisationPK id;
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

    public AccountOrganisations() {
        this.deleted = false;
    }

    public AccountOrganisations(AccountOrganisationPK id) {
        this.id = id;
    }
}
