package com.pinterest.contentservice.controller;

import com.pinterest.contentservice.dto.ApiResponse;
import com.pinterest.contentservice.dto.PinDto;
import com.pinterest.contentservice.dto.PinRequest;
import com.pinterest.contentservice.service.PinService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/content/pins")
@RequiredArgsConstructor
public class PinController {

    private final PinService pinService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<PinDto>> createPin(@Valid @RequestBody PinRequest pinRequest) {
        PinDto createdPin = pinService.createPin(pinRequest);
        return new ResponseEntity<>(ApiResponse.success("Pin created successfully", createdPin), HttpStatus.CREATED);
    }
    
    @GetMapping("/{pinId}")
    public ResponseEntity<ApiResponse<PinDto>> getPinById(@PathVariable Long pinId) {
        PinDto pinDto = pinService.getPinById(pinId);
        return ResponseEntity.ok(ApiResponse.success(pinDto));
    }
    
    @PutMapping("/{pinId}")
    public ResponseEntity<ApiResponse<PinDto>> updatePin(
            @PathVariable Long pinId,
            @Valid @RequestBody PinRequest pinRequest) {
        PinDto updatedPin = pinService.updatePin(pinId, pinRequest);
        return ResponseEntity.ok(ApiResponse.success("Pin updated successfully", updatedPin));
    }
    
    @DeleteMapping("/{pinId}")
    public ResponseEntity<ApiResponse<Void>> deletePin(@PathVariable Long pinId) {
        pinService.deletePin(pinId);
        return ResponseEntity.ok(ApiResponse.success("Pin deleted successfully", null));
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<PinDto>>> getPinsByUserId(@PathVariable Long userId) {
        List<PinDto> pins = pinService.getPinsByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(pins));
    }
    
    @GetMapping("/user/{userId}/paged")
    public ResponseEntity<ApiResponse<Page<PinDto>>> getPinsByUserIdPaged(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<PinDto> pins = pinService.getPinsByUserId(userId, pageable);
        
        return ResponseEntity.ok(ApiResponse.success(pins));
    }
    
    @GetMapping("/board/{boardId}")
    public ResponseEntity<ApiResponse<List<PinDto>>> getPinsByBoardId(@PathVariable Long boardId) {
        List<PinDto> pins = pinService.getPinsByBoardId(boardId);
        return ResponseEntity.ok(ApiResponse.success(pins));
    }
    
    @GetMapping("/board/{boardId}/paged")
    public ResponseEntity<ApiResponse<Page<PinDto>>> getPinsByBoardIdPaged(
            @PathVariable Long boardId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<PinDto> pins = pinService.getPinsByBoardId(boardId, pageable);
        
        return ResponseEntity.ok(ApiResponse.success(pins));
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<PinDto>>> searchPins(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<PinDto> pins = pinService.searchPins(query, pageable);
        
        return ResponseEntity.ok(ApiResponse.success(pins));
    }
    
    @GetMapping("/user/{userId}/drafts")
    public ResponseEntity<ApiResponse<List<PinDto>>> getDraftPinsByUserId(@PathVariable Long userId) {
        List<PinDto> pins = pinService.getDraftPinsByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(pins));
    }
    
    @PutMapping("/{pinId}/publish")
    public ResponseEntity<ApiResponse<PinDto>> publishDraftPin(@PathVariable Long pinId) {
        PinDto publishedPin = pinService.publishDraftPin(pinId);
        return ResponseEntity.ok(ApiResponse.success("Pin published successfully", publishedPin));
    }
    
    @PutMapping("/{pinId}/board/{boardId}")
    public ResponseEntity<ApiResponse<PinDto>> savePinToBoard(
            @PathVariable Long pinId,
            @PathVariable Long boardId) {
        PinDto savedPin = pinService.savePinToBoard(pinId, boardId);
        return ResponseEntity.ok(ApiResponse.success("Pin saved to board successfully", savedPin));
    }
}