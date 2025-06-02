package com.pinterest.businessservice.service;

import com.pinterest.businessservice.dto.SponsoredPinDto;
import com.pinterest.businessservice.model.SponsoredPin.SponsoredStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface SponsoredPinService {
    
    SponsoredPinDto createSponsoredPin(SponsoredPinDto sponsoredPinDto);
    
    SponsoredPinDto getSponsoredPinById(Long id);
    
    List<SponsoredPinDto> getSponsoredPinsByBusinessProfileId(Long businessProfileId);
    
    Page<SponsoredPinDto> getSponsoredPinsByBusinessProfileId(Long businessProfileId, Pageable pageable);
    
    List<SponsoredPinDto> getSponsoredPinsByCampaignId(Long campaignId);
    
    Page<SponsoredPinDto> getSponsoredPinsByCampaignId(Long campaignId, Pageable pageable);
    
    SponsoredPinDto updateSponsoredPin(Long id, SponsoredPinDto sponsoredPinDto);
    
    void deleteSponsoredPin(Long id);
    
    Page<SponsoredPinDto> getSponsoredPinsByStatus(SponsoredStatus status, Pageable pageable);
    
    List<SponsoredPinDto> getActiveSponsoredPins();
    
    Page<SponsoredPinDto> getActiveSponsoredPins(Pageable pageable);
    
    SponsoredPinDto updateSponsoredPinStatus(Long id, SponsoredStatus status);
    
    void recordImpression(Long id);
    
    void recordClick(Long id);
    
    void recordSave(Long id);
    
    List<SponsoredPinDto> getSponsoredPinsByDateRange(LocalDate startDate, LocalDate endDate);
    
    Page<SponsoredPinDto> searchSponsoredPins(String keyword, Pageable pageable);
}