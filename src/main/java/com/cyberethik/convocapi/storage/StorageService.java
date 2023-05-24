// 
// Decompiled by Procyon v0.5.36
// 

package com.cyberethik.convocapi.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService
{
    void init();

    void store(final MultipartFile file);
    
    void storeFile(final MultipartFile file);
    
    Stream<Path> loadAll();
    
    Path load(final String filename);
    
    Long size(final String filename);
    
    Resource loadAsResource(final String filename);
    
    void deleteAll();
    
    boolean deleteOne(final String file_name);
    
    boolean exist(final String file_name);
    
    String rename(final String file_name);
    
    String rename(final String file_name, final Long id);
}
