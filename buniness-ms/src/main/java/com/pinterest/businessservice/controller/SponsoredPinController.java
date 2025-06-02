package com.pinterest.businessservice.controller;

import com.pinterest.businessservice.dto.SponsoredPinDto;
import com.pinterest.businessservice.model.ApiResponse;
import com.pinterest.businessservice.model.SponsoredPin.SponsoredStatus;
import com.pinterest.businessservice.service.SponsoredPinService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/sponsored-pins")
@RequiredArgsConstructor
public class SponsoredPinController {

    private final SponsoredPinService sponsoredPinService;

    @PostMapping
    public ResponseEntity<ApiResponse<SponsoredPinDto>> createSponsoredPin(@Valid @RequestBody SponsoredPinDto sponsoredPinDto) {
        SponsoredPinDto createdPin = sponsoredPinService.createSponsoredPin(sponsoredPinDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Sponsored pin created successfully", createdPin));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SponsoredPinDto>> getSponsoredPinById(@PathVariable Long id) {
        SponsoredPinDto sponsoredPin = sponsoredPinService.getSponsoredPinById(id);
        return ResponseEntity.ok(ApiResponse.success("Sponsored pin retrieved successfully", sponsoredPin));
    }

    @GetMapping("/business/{businessProfileId}")
    public ResponseEntity<ApiResponse<List<SponsoredPinDto>>> getSponsoredPinsByBusinessProfileId(
            @PathVariable Long businessProfileId) {
        
        List<SponsoredPinDto> sponsoredPins = sponsoredPinService.getSponsoredPinsByBusinessProfileId(businessProfileId);
        return ResponseEntity.ok(ApiResponse.success("Sponsored pins retrieved successfully", sponsoredPins));
    }

    @GetMapping("/business/{businessProfileId}/paged")
    public ResponseEntity<ApiResponse<Page<SponsoredPinDto>>> getPagedSponsoredPinsByBusinessProfileId(
            @PathVariable Long businessProfileId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<SponsoredPinDto> sponsoredPinsPage = sponsoredPinService.getSponsoredPinsByBusinessProfileId(businessProfileId, pageable);
        return ResponseEntity.ok(ApiResponse.success("Sponsored pins retrieved successfully", sponsoredPinsPage));
    }

    @GetMapping("/campaign/{campaignId}")
    public ResponseEntity<ApiResponse<List<SponsoredPinDto>>> getSponsoredPinsByCampaignId(
            @PathVariable Long campaignId) {
        
        List<SponsoredPinDto> sponsoredPins = sponsoredPinService.getSponsoredPinsByCampaignId(campaignId);
        return ResponseEntity.ok(ApiResponse.success("Sponsored pins retrieved successfully", sponsoredPins));
    }

    @GetMapping("/campaign/{campaignId}/paged")
    public ResponseEntity<ApiResponse<Page<SponsoredPinDto>>> getPagedSponsoredPinsByCampaignId(
            @PathVariable Long campaignId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<SponsoredPinDto> sponsoredPinsPage = sponsoredPinService.getSponsoredPinsByCampaignId(campaignId, pageable);
        return ResponseEntity.ok(ApiResponse.success("Sponsored pins retrieved successfully", sponsoredPinsPage));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<Page<SponsoredPinDto>>> getSponsoredPinsByStatus(
            @PathVariable SponsoredStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<SponsoredPinDto> sponsoredPinsPage = sponsoredPinService.getSponsoredPinsByStatus(status, pageable);
        return ResponseEntity.ok(ApiResponse.success("Sponsored pins retrieved successfully", sponsoredPinsPage));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<SponsoredPinDto>>> getActiveSponsoredPins() {
        List<SponsoredPinDto> activePins = sponsoredPinService.getActiveSponsoredPins();
        return ResponseEntity.ok(ApiResponse.success("Active sponsored pins retrieved successfully", activePins));
    }

    @GetMapping("/active/paged")
    public ResponseEntity<ApiResponse<Page<SponsoredPinDto>>> getPagedActiveSponsoredPins(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<SponsoredPinDto> activePinsPage = sponsoredPinService.getActiveSponsoredPins(pageable);
        return ResponseEntity.ok(ApiResponse.success("Active sponsored pins retrieved successfully", activePinsPage));
    }

    @GetMapping("/date-range")
    public ResponseEntity<ApiResponse<List<SponsoredPinDto>>> getSponsoredPinsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        List<SponsoredPinDto> sponsoredPins = sponsoredPinService.getSponsoredPinsByDateRange(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Sponsored pins retrieved successfully", sponsoredPins));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<SponsoredPinDto>>> searchSponsoredPins(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<SponsoredPinDto> searchResults = sponsoredPinService.searchSponsoredPins(keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success("Search results retrieved successfully", searchResults));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SponsoredPinDto>> updateSponsoredPin(
            @PathVariable Long id,
            @Valid @RequestBody SponsoredPinDto sponsoredPinDto) {
        
        SponsoredPinDto updatedPin = sponsoredPinService.updateSponsoredPin(id, sponsoredPinDto);
        return ResponseEntity.ok(ApiResponse.success("Sponsored pin updated successfully", updatedPin));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<SponsoredPinDto>> updateSponsoredPinStatus(
            @PathVariable Long id,
            @RequestParam SponsoredStatus status) {
        
        SponsoredPinDto updatedPin = sponsoredPinService.updateSponsoredPinStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Status updated successfully", updatedPin));
    }

    @PostMapping("/{id}/impression")
    public ResponseEntity<ApiResponse<Void>> recordImpression(@PathVariable Long id) {
        sponsoredPinService.recordImpression(id);
        return ResponseEntity.ok(ApiResponse.success("Impression recorded successfully", null));
    }

    @PostMapping("/{id}/click")
    public ResponseEntity<ApiResponse<Void>> recordClick(@PathVariable Long id) {
        sponsoredPinService.recordClick(id);
        return ResponseEntity.ok(ApiResponse.success("Click recorded successfully", null));
    }

    @PostMapping("/{id}/save")
    public ResponseEntity<ApiResponse<Void>> recordSave(@PathVariable Long id) {
        sponsoredPinService.recordSave(id);
        return ResponseEntity.ok(ApiResponse.success("Save recorded successfully", null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSponsoredPin(@PathVariable Long id) {
        sponsoredPinService.deleteSponsoredPin(id);
        return ResponseEntity.ok(ApiResponse.success("Sponsored pin deleted successfully", null));
    }
}