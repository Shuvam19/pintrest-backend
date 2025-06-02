package com.pinterest.businessservice.controller;

import com.pinterest.businessservice.dto.BusinessProfileDto;
import com.pinterest.businessservice.model.ApiResponse;
import com.pinterest.businessservice.model.BusinessProfile.BusinessCategory;
import com.pinterest.businessservice.model.BusinessProfile.VerificationStatus;
import com.pinterest.businessservice.service.BusinessProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/business-profiles")
@RequiredArgsConstructor
public class BusinessProfileController {

    private final BusinessProfileService businessProfileService;

    @PostMapping
    public ResponseEntity<ApiResponse<BusinessProfileDto>> createBusinessProfile(
            @Valid @RequestBody BusinessProfileDto businessProfileDto) {
        BusinessProfileDto createdProfile = businessProfileService.createBusinessProfile(businessProfileDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Business profile created successfully", createdProfile));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BusinessProfileDto>> getBusinessProfileById(@PathVariable Long id) {
        BusinessProfileDto businessProfile = businessProfileService.getBusinessProfileById(id);
        return ResponseEntity.ok(ApiResponse.success("Business profile retrieved successfully", businessProfile));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<BusinessProfileDto>> getBusinessProfileByUserId(@PathVariable Long userId) {
        BusinessProfileDto businessProfile = businessProfileService.getBusinessProfileByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("Business profile retrieved successfully", businessProfile));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<BusinessProfileDto>>> getAllBusinessProfiles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "businessName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<BusinessProfileDto> businessProfiles = businessProfileService.getAllBusinessProfiles(pageable);
        return ResponseEntity.ok(ApiResponse.success("Business profiles retrieved successfully", businessProfiles));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<Page<BusinessProfileDto>>> getActiveBusinessProfiles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<BusinessProfileDto> activeProfiles = businessProfileService.getBusinessProfilesByActive(true, pageable);
        return ResponseEntity.ok(ApiResponse.success("Active business profiles retrieved successfully", activeProfiles));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<Page<BusinessProfileDto>>> getBusinessProfilesByCategory(
            @PathVariable BusinessCategory category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<BusinessProfileDto> profilesByCategory = businessProfileService.getBusinessProfilesByCategory(category, pageable);
        return ResponseEntity.ok(ApiResponse.success("Business profiles by category retrieved successfully", profilesByCategory));
    }

    @GetMapping("/verification/{status}")
    public ResponseEntity<ApiResponse<Page<BusinessProfileDto>>> getBusinessProfilesByVerificationStatus(
            @PathVariable VerificationStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<BusinessProfileDto> profilesByStatus = businessProfileService.getBusinessProfilesByVerificationStatus(status, pageable);
        return ResponseEntity.ok(ApiResponse.success("Business profiles by verification status retrieved successfully", profilesByStatus));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<BusinessProfileDto>>> searchBusinessProfiles(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<BusinessProfileDto> searchResults = businessProfileService.searchBusinessProfiles(keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success("Search results retrieved successfully", searchResults));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BusinessProfileDto>> updateBusinessProfile(
            @PathVariable Long id,
            @Valid @RequestBody BusinessProfileDto businessProfileDto) {
        
        BusinessProfileDto updatedProfile = businessProfileService.updateBusinessProfile(id, businessProfileDto);
        return ResponseEntity.ok(ApiResponse.success("Business profile updated successfully", updatedProfile));
    }

    @PatchMapping("/{id}/verification")
    public ResponseEntity<ApiResponse<BusinessProfileDto>> updateVerificationStatus(
            @PathVariable Long id,
            @RequestParam VerificationStatus status) {
        
        BusinessProfileDto updatedProfile = businessProfileService.updateVerificationStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Verification status updated successfully", updatedProfile));
    }

    @PatchMapping("/{id}/toggle-active")
    public ResponseEntity<ApiResponse<BusinessProfileDto>> toggleActiveStatus(@PathVariable Long id) {
        BusinessProfileDto updatedProfile = businessProfileService.toggleActiveStatus(id);
        return ResponseEntity.ok(ApiResponse.success("Active status toggled successfully", updatedProfile));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBusinessProfile(@PathVariable Long id) {
        businessProfileService.deleteBusinessProfile(id);
        return ResponseEntity.ok(ApiResponse.success("Business profile deleted successfully", null));
    }
}