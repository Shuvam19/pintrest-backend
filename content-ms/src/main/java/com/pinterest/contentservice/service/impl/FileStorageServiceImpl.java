package com.pinterest.contentservice.service.impl;

import com.pinterest.contentservice.service.FileStorageService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;
    
    private Path fileStoragePath;
    
    @PostConstruct
    public void init() {
        this.fileStoragePath = Paths.get(uploadDir).toAbsolutePath().normalize();
        
        try {
            Files.createDirectories(this.fileStoragePath);
        } catch (IOException e) {
            log.error("Could not create the directory where the uploaded files will be stored", e);
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored", e);
        }
    }

    @Override
    public String storeFile(MultipartFile file) throws IOException {
        // Normalize file name
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        
        // Check if the file's name contains invalid characters
        if (originalFileName.contains("..")) {
            throw new IllegalArgumentException("Filename contains invalid path sequence " + originalFileName);
        }
        
        // Generate a unique file name to prevent duplicates
        String fileExtension = "";
        if (originalFileName.contains(".")) {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        String fileName = UUID.randomUUID().toString() + fileExtension;
        
        // Copy file to the target location
        Path targetLocation = this.fileStoragePath.resolve(fileName);
        
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
        }
        
        // Create a URL to access the file
        String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/content/files/")
                .path(fileName)
                .toUriString();
        
        log.info("File stored successfully: {}", fileUrl);
        return fileUrl;
    }

    @Override
    public boolean deleteFile(String fileUrl) {
        try {
            // Extract file name from URL
            String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            Path filePath = this.fileStoragePath.resolve(fileName).normalize();
            
            // Check if file exists and is within the upload directory
            if (!filePath.toAbsolutePath().startsWith(this.fileStoragePath.toAbsolutePath())) {
                throw new IllegalArgumentException("File is not in the upload directory");
            }
            
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.error("Error deleting file: {}", fileUrl, e);
            return false;
        }
    }
}