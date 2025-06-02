package com.pinterest.businessservice.controller;

import com.pinterest.businessservice.dto.CampaignDto;
import com.pinterest.businessservice.model.ApiResponse;
import com.pinterest.businessservice.model.Campaign.CampaignObjective;
import com.pinterest.businessservice.model.Campaign.CampaignStatus;
import com.pinterest.businessservice.service.CampaignService;
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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/campaigns")
@RequiredArgsConstructor
public class CampaignController {

    private final CampaignService campaignService;

    @PostMapping
    public ResponseEntity<ApiResponse<CampaignDto>> createCampaign(@Valid @RequestBody CampaignDto campaignDto) {
        CampaignDto createdCampaign = campaignService.createCampaign(campaignDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Campaign created successfully", createdCampaign));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CampaignDto>> getCampaignById(@PathVariable Long id) {
        CampaignDto campaign = campaignService.getCampaignById(id);
        return ResponseEntity.ok(ApiResponse.success("Campaign retrieved successfully", campaign));
    }

    @GetMapping("/business/{businessProfileId}")
    public ResponseEntity<ApiResponse<List<CampaignDto>>> getCampaignsByBusinessProfileId(
            @PathVariable Long businessProfileId) {
        
        List<CampaignDto> campaigns = campaignService.getCampaignsByBusinessProfileId(businessProfileId);
        return ResponseEntity.ok(ApiResponse.success("Campaigns retrieved successfully", campaigns));
    }

    @GetMapping("/business/{businessProfileId}/paged")
    public ResponseEntity<ApiResponse<Page<CampaignDto>>> getPagedCampaignsByBusinessProfileId(
            @PathVariable Long businessProfileId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<CampaignDto> campaignsPage = campaignService.getCampaignsByBusinessProfileId(businessProfileId, pageable);
        return ResponseEntity.ok(ApiResponse.success("Campaigns retrieved successfully", campaignsPage));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<Page<CampaignDto>>> getCampaignsByStatus(
            @PathVariable CampaignStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<CampaignDto> campaignsPage = campaignService.getCampaignsByStatus(status, pageable);
        return ResponseEntity.ok(ApiResponse.success("Campaigns retrieved successfully", campaignsPage));
    }

    @GetMapping("/objective/{objective}")
    public ResponseEntity<ApiResponse<Page<CampaignDto>>> getCampaignsByObjective(
            @PathVariable CampaignObjective objective,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<CampaignDto> campaignsPage = campaignService.getCampaignsByObjective(objective, pageable);
        return ResponseEntity.ok(ApiResponse.success("Campaigns retrieved successfully", campaignsPage));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<CampaignDto>>> getActiveCampaigns() {
        List<CampaignDto> activeCampaigns = campaignService.getActiveCampaigns();
        return ResponseEntity.ok(ApiResponse.success("Active campaigns retrieved successfully", activeCampaigns));
    }

    @GetMapping("/active/paged")
    public ResponseEntity<ApiResponse<Page<CampaignDto>>> getPagedActiveCampaigns(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<CampaignDto> activeCampaignsPage = campaignService.getActiveCampaigns(pageable);
        return ResponseEntity.ok(ApiResponse.success("Active campaigns retrieved successfully", activeCampaignsPage));
    }

    @GetMapping("/scheduled")
    public ResponseEntity<ApiResponse<List<CampaignDto>>> getScheduledCampaigns() {
        List<CampaignDto> scheduledCampaigns = campaignService.getScheduledCampaigns();
        return ResponseEntity.ok(ApiResponse.success("Scheduled campaigns retrieved successfully", scheduledCampaigns));
    }

    @GetMapping("/to-complete")
    public ResponseEntity<ApiResponse<List<CampaignDto>>> getCampaignsToComplete() {
        List<CampaignDto> campaignsToComplete = campaignService.getCampaignsToComplete();
        return ResponseEntity.ok(ApiResponse.success("Campaigns to complete retrieved successfully", campaignsToComplete));
    }

    @GetMapping("/date-range")
    public ResponseEntity<ApiResponse<List<CampaignDto>>> getCampaignsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        List<CampaignDto> campaigns = campaignService.getCampaignsByDateRange(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Campaigns retrieved successfully", campaigns));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<CampaignDto>>> searchCampaigns(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<CampaignDto> searchResults = campaignService.searchCampaigns(keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success("Search results retrieved successfully", searchResults));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CampaignDto>> updateCampaign(
            @PathVariable Long id,
            @Valid @RequestBody CampaignDto campaignDto) {
        
        CampaignDto updatedCampaign = campaignService.updateCampaign(id, campaignDto);
        return ResponseEntity.ok(ApiResponse.success("Campaign updated successfully", updatedCampaign));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<CampaignDto>> updateCampaignStatus(
            @PathVariable Long id,
            @RequestParam CampaignStatus status) {
        
        CampaignDto updatedCampaign = campaignService.updateCampaignStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Status updated successfully", updatedCampaign));
    }

    @PostMapping("/{id}/impression")
    public ResponseEntity<ApiResponse<Void>> recordImpression(@PathVariable Long id) {
        campaignService.recordImpression(id);
        return ResponseEntity.ok(ApiResponse.success("Impression recorded successfully", null));
    }

    @PostMapping("/{id}/click")
    public ResponseEntity<ApiResponse<Void>> recordClick(@PathVariable Long id) {
        campaignService.recordClick(id);
        return ResponseEntity.ok(ApiResponse.success("Click recorded successfully", null));
    }

    @PostMapping("/{id}/conversion")
    public ResponseEntity<ApiResponse<Void>> recordConversion(@PathVariable Long id) {
        campaignService.recordConversion(id);
        return ResponseEntity.ok(ApiResponse.success("Conversion recorded successfully", null));
    }

    @PatchMapping("/{id}/amount-spent")
    public ResponseEntity<ApiResponse<CampaignDto>> updateAmountSpent(
            @PathVariable Long id,
            @RequestParam BigDecimal amount) {
        
        CampaignDto updatedCampaign = campaignService.updateAmountSpent(id, amount);
        return ResponseEntity.ok(ApiResponse.success("Amount spent updated successfully", updatedCampaign));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCampaign(@PathVariable Long id) {
        campaignService.deleteCampaign(id);
        return ResponseEntity.ok(ApiResponse.success("Campaign deleted successfully", null));
    }
}