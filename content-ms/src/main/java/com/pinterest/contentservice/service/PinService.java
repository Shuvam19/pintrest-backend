package com.pinterest.contentservice.service;

import com.pinterest.contentservice.dto.PinDto;
import com.pinterest.contentservice.dto.PinRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PinService {
    
    // Create a new pin
    PinDto createPin(PinRequest pinRequest);
    
    // Get a pin by ID
    PinDto getPinById(Long pinId);
    
    // Update an existing pin
    PinDto updatePin(Long pinId, PinRequest pinRequest);
    
    // Delete a pin
    void deletePin(Long pinId);
    
    // Get all pins by user ID
    List<PinDto> getPinsByUserId(Long userId);
    
    // Get pins by user ID with pagination
    Page<PinDto> getPinsByUserId(Long userId, Pageable pageable);
    
    // Get all pins by board ID
    List<PinDto> getPinsByBoardId(Long boardId);
    
    // Get pins by board ID with pagination
    Page<PinDto> getPinsByBoardId(Long boardId, Pageable pageable);
    
    // Search pins by keyword
    Page<PinDto> searchPins(String searchTerm, Pageable pageable);
    
    // Get draft pins by user ID
    List<PinDto> getDraftPinsByUserId(Long userId);
    
    // Publish a draft pin
    PinDto publishDraftPin(Long pinId);
    
    // Save pin to a different board
    PinDto savePinToBoard(Long pinId, Long boardId);
}