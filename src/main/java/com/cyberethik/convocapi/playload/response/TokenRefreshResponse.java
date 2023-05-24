// 
// Decompiled by Procyon v0.5.36
// 

package com.cyberethik.convocapi.playload.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TokenRefreshResponse
{
    private String token;
    private String refreshToken;
    private String tokenType;
    
    public TokenRefreshResponse(final String token, final String refreshToken) {
        this.tokenType = "Bearer";
        this.token = token;
        this.refreshToken = refreshToken;
    }

}
