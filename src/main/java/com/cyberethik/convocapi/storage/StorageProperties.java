// 
// Decompiled by Procyon v0.5.36
// 

package com.cyberethik.convocapi.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("storage")
public class StorageProperties
{
    private String location;
    
    public StorageProperties() {
        this.location = "upload-dir";
    }
    
    public String getLocation() {
        return this.location;
    }
    
    public void setLocation(final String location) {
        this.location = location;
    }
}
