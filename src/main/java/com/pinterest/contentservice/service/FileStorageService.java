package com.pinterest.contentservice.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileStorageService {
    
    /**
     * Store a file in the local file system
     * 
     * @param file The file to store
     * @return The URL to access the file
     * @throws IOException If an error occurs during file storage
     */
    String storeFile(MultipartFile file) throws IOException;
    
    /**
     * Delete a file from the local file system
     * 
     * @param fileUrl The URL of the file to delete
     * @return true if the file was deleted successfully, false otherwise
     */
    boolean deleteFile(String fileUrl);
}