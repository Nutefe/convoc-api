// 
// Decompiled by Procyon v0.5.36
// 

package com.cyberethik.convocapi.persistance.service.impl;


import com.cyberethik.convocapi.exception.TokenRefreshException;
import com.cyberethik.convocapi.persistance.entities.RefreshTokens;
import com.cyberethik.convocapi.persistance.repository.AccountRepository;
import com.cyberethik.convocapi.persistance.repository.RefreshTokenRepository;
import com.cyberethik.convocapi.persistance.service.dao.RefreshTokenDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenImp implements RefreshTokenDao
{
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private AccountRepository accountRepository;

    @Value("${convoc.app.jwtExpirationMs}")
    private int jwtExpirationInMs;
    @Value("${convoc.app.refreshExpirationDateInMs}")
    private Long refreshTokenDurationMs;
    
    @Override
    public Optional<RefreshTokens> findByToken(final String token) {
        return this.refreshTokenRepository.findByToken(token);
    }
    
    @Override
    public RefreshTokens createRefreshToken(final String email) {
        RefreshTokens refreshTokens = new RefreshTokens();
        refreshTokens.setAccount(this.accountRepository.findByEmailAndActifTrueAndDeletedFalse(email).orElseThrow(() -> new RuntimeException("Erreur: email n'existe pas ou votre compte est inactif soit banni.")));
        refreshTokens.setExpiryDate(Instant.now().plusMillis(this.refreshTokenDurationMs));
        refreshTokens.setToken(UUID.randomUUID().toString());
        refreshTokens = this.refreshTokenRepository.save(refreshTokens);
        return refreshTokens;
    }
    
    @Override
    public RefreshTokens verifyExpiration(final RefreshTokens token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            this.refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new signin request");
        }
        return token;
    }
    
    @Override
    public int deleteByUserId(final Long userId) {
        return 0;
    }
}
