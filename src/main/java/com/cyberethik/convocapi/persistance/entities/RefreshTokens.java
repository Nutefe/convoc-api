package com.cyberethik.convocapi.persistance.entities;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Data
@Entity
@Table(name = "refreshtokens")
@EntityListeners({ AuditingEntityListener.class })
public class RefreshTokens implements Serializable
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    @JoinColumn(name = "account", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Accounts account;
    @Column(nullable = false, unique = true)
    private String token;
    @Column(nullable = false)
    private Instant expiryDate;
    
    public RefreshTokens() {
    }
    
    public RefreshTokens(final long id, final Accounts account, final String token, final Instant expiryDate) {
        this.id = id;
        this.account = account;
        this.token = token;
        this.expiryDate = expiryDate;
    }

}
