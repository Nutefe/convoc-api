// 
// Decompiled by Procyon v0.5.36
// 

package com.cyberethik.convocapi.persistance.repository;

import com.cyberethik.convocapi.persistance.entities.RefreshTokens;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokens, Long>
{
    Optional<RefreshTokens> findById(final Long id);
    
    Optional<RefreshTokens> findByToken(final String token);
}
