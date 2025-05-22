// 
// Decompiled by Procyon v0.5.36
// 

package com.cyberethik.convocapi.playload.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class UpdatePassRequest
{
    @NotBlank
    private String nouveau;
    @NotBlank
    @Size(min = 6, max = 40)
    private String ancien;

}
