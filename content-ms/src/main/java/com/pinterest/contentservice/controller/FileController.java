package com.pinterest.contentservice.controller;

import com.pinterest.contentservice.dto.ApiResponse;
import com.pinterest.contentservice.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/content/files")
@RequiredArgsConstructor
@Slf4j
public class FileController {

    private final FileStorageService fileStorageService;
    
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadFile(
            @RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = fileStorageService.storeFile(file);
            
            Map<String, String> response = new HashMap<>();
            response.put("fileUrl", fileUrl);
            
            return ResponseEntity.ok(ApiResponse.success("File uploaded successfully", response));
        } catch (IOException e) {
            log.error("Failed to upload file", e);
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to upload file: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        try {
            // Load file as Resource
            Path filePath = Paths.get(System.getProperty("user.dir") + "/uploads/" + fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            
            // Check if file exists and is readable
            if (resource.exists() && resource.isReadable()) {
                // Try to determine file's content type
                String contentType = null;
                try {
                    contentType = Files.probeContentType(filePath);
                } catch (IOException e) {
                    log.error("Could not determine file type", e);
                }
                
                // Fallback to the default content type if type could not be determined
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }
                
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            log.error("File not found: {}", fileName, e);
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{fileName:.+}")
    public ResponseEntity<ApiResponse<Void>> deleteFile(@PathVariable String fileName) {
        String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/content/files/")
                .path(fileName)
                .toUriString();
        
        boolean deleted = fileStorageService.deleteFile(fileUrl);
        
        if (deleted) {
            return ResponseEntity.ok(ApiResponse.success("File deleted successfully", null));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to delete file"));
        }
    }
}