// 
// Decompiled by Procyon v0.5.36
// 

package com.cyberethik.convocapi.controller;

import com.cyberethik.convocapi.storage.FileSystemStorageDocService;
import com.cyberethik.convocapi.storage.FileSystemStorageResourceService;
import com.cyberethik.convocapi.storage.FileSystemStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping({ "/web/service" })
public class FileController
{
    private static final Logger logger;
    @Autowired
    private FileSystemStorageService fileStorageService;
    @Autowired
    private FileSystemStorageDocService fileSystemStorageDocService;
    @Autowired
    private FileSystemStorageResourceService fileSystemStorageResourceService;
    
    @PostMapping({ "/uploadFile" })
    public void uploadFile(@RequestParam("file") final MultipartFile file) {
        this.fileStorageService.storeFile(file);
    }
    
    @PostMapping({ "/uploadMultiFile" })
    public void uploadMultiFile(@RequestParam("file") final MultipartFile[] file) {
        for (int i = 0; i < file.length; ++i) {
            this.fileStorageService.storeFile(file[i]);
        }
    }
    
    @GetMapping({ "/downloadFile/{fileName:.+}" })
    public ResponseEntity<Resource> downloadFile(@PathVariable final String fileName, final HttpServletRequest request) {
        // Load file as Resource
        Resource resource = fileStorageService.loadAsResource(fileName);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
    @GetMapping({ "/downloadFile/documents/{fileName:.+}" })
    public ResponseEntity<Resource> document(@PathVariable final String fileName, final HttpServletRequest request) {
        // Load file as Resource
        Resource resource = fileSystemStorageDocService.loadAsResource(fileName);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
    @GetMapping({ "/downloadFile/resources/{fileName:.+}" })
    public ResponseEntity<Resource> resource(@PathVariable final String fileName, final HttpServletRequest request) {
        // Load file as Resource
        Resource resource = fileSystemStorageResourceService.loadAsResource(fileName);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
    
    @GetMapping({ "/downloadAvatar/{fileName:.+}" })
    public ResponseEntity<Resource> downloadAvatar(@PathVariable final String fileName, final HttpServletRequest request) {
        // Load file as Resource
        Resource resource = fileStorageService.loadAsResource(fileName);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
    
    @RequestMapping(value = { "/files/{file_name}" }, method = { RequestMethod.DELETE })
    public String delete(@PathVariable("file_name") final String file_name) {
        if (this.fileStorageService.deleteOne(file_name)) {
            return "Supprimer avec success";
        }
        return "erreur de suppression";
    }
    
    static {
        logger = LoggerFactory.getLogger((Class)FileController.class);
    }
}
