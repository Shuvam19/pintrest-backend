package com.pinterest.businessservice.service;

import com.pinterest.businessservice.dto.CampaignDto;
import com.pinterest.businessservice.model.Campaign.CampaignObjective;
import com.pinterest.businessservice.model.Campaign.CampaignStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface CampaignService {
    
    CampaignDto createCampaign(CampaignDto campaignDto);
    
    CampaignDto getCampaignById(Long id);
    
    List<CampaignDto> getCampaignsByBusinessProfileId(Long businessProfileId);
    
    Page<CampaignDto> getCampaignsByBusinessProfileId(Long businessProfileId, Pageable pageable);
    
    CampaignDto updateCampaign(Long id, CampaignDto campaignDto);
    
    void deleteCampaign(Long id);
    
    Page<CampaignDto> getCampaignsByStatus(CampaignStatus status, Pageable pageable);
    
    Page<CampaignDto> getCampaignsByObjective(CampaignObjective objective, Pageable pageable);
    
    List<CampaignDto> getActiveCampaigns();
    
    Page<CampaignDto> getActiveCampaigns(Pageable pageable);
    
    CampaignDto updateCampaignStatus(Long id, CampaignStatus status);
    
    void recordImpression(Long id);
    
    void recordClick(Long id);
    
    void recordConversion(Long id);
    
    void updateAmountSpent(Long id, BigDecimal amount);
    
    List<CampaignDto> getCampaignsByDateRange(LocalDate startDate, LocalDate endDate);
    
    Page<CampaignDto> searchCampaigns(String keyword, Pageable pageable);
    
    List<CampaignDto> getScheduledCampaigns();
    
    List<CampaignDto> getCampaignsToComplete();
}