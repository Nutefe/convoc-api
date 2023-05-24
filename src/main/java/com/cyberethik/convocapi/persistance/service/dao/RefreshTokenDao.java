// 
// Decompiled by Procyon v0.5.36
// 

package com.cyberethik.convocapi.persistance.service.dao;


import com.cyberethik.convocapi.persistance.entities.RefreshTokens;

import java.util.Optional;

public interface RefreshTokenDao
{
    Optional<RefreshTokens> findByToken(final String token);
    
    RefreshTokens createRefreshToken(final String email);
    
    RefreshTokens verifyExpiration(final RefreshTokens token);
    
    int deleteByUserId(final Long userId);
}
