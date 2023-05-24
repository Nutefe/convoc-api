// 
// Decompiled by Procyon v0.5.36
// 

package com.cyberethik.convocapi.storage;

import com.cyberethik.convocapi.playload.helper.Helpers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

@Service
public class FileSystemStorageResourceService implements StorageService
{
    private final Path rootLocation;
    private String path;

    @Autowired
    public FileSystemStorageResourceService() {
        this.path = Helpers.path_file_resources;
        this.rootLocation = Paths.get(this.path);
    }
    
    public void store( MultipartFile file) {
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file " + filename);
            }
            if (filename.contains("..")) {
                // This is a security check
                throw new StorageException(
                        "Cannot store file with relative path outside current directory "
                                + filename);
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, this.rootLocation.resolve(filename),
                        StandardCopyOption.REPLACE_EXISTING);
            }
        }
        catch (IOException e) {
            throw new StorageException("Failed to store file " + filename, e);
        }
    }
    
    public void storeFile( MultipartFile file) {
        if (file.isEmpty()){
            throw new StorageException("Failed to store empty file ");
        }
        try {
            String fileName = file.getOriginalFilename();
            InputStream is = file.getInputStream();
            Files.copy(is, Paths.get(path+fileName), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e){
            throw new StorageException(String.format("Failed to store empty file", file.getName()), e);
        }
    }
    
    public Long size( String filename) {
        final Path file = this.load(filename);
        final File file2 = file.toFile();
        if (file2.isFile()) {
            return file2.length();
        }
        return 0L;
    }
    
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(this.rootLocation::relativize);
        }
        catch (IOException e) {
            throw new StorageException("Failed to read stored files", e);
        }
    }
    
    public Path load( String filename) {
        return this.rootLocation.resolve(filename);
    }
    
    public Resource loadAsResource( String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
                throw new StorageFileNotFoundException(
                        "Could not read file: " + filename);

            }
        }
        catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }
    
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(this.rootLocation.toFile());
    }
    
    public boolean deleteOne( String file_name) {
        try {
            Path path = load(file_name);
            File file = path.toFile();

            if (file.delete()){
                System.out.println(file.getName()+" is deleted");
                return true;
            } else {
                System.out.println("Delete operation is failed");
                return false;
            }
        } catch (Exception e){
            return false;
        }
    }
    
    public boolean exist( String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return true;
            }
            else {
                throw new StorageFileNotFoundException(
                        "Could not read file: " + filename);
            }
        }
        catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }
    
    public String rename( String filename) {
        String ext = filename.substring(filename.lastIndexOf(".")+1);
        String name = Helpers.generat();
        File f1 = new File(path+filename);
        File f2 = new File(path+name+ext);
        boolean b = f1.renameTo(f2);
        if (b){
            System.out.println(b);
            return name+"."+ext;
        }else {
            throw new StorageFileNotFoundException(
                    "Could not read file: " + filename);
        }
    }
    
    public String rename( String filename, final Long id) {
        String ext = filename.substring(filename.lastIndexOf(".")+1);
        String name = Helpers.generat();
        File f1 = new File(path+filename);
        File f2 = new File(path+name+id+"."+ext);
        boolean b = f1.renameTo(f2);
        if (b){
            return name+id+"."+ext;
        }else {
            throw new StorageFileNotFoundException(
                    "Could not read file: " + filename);
        }
    }

    public void init() {
        try {
            Files.createDirectories(rootLocation);
        }
        catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }
}
