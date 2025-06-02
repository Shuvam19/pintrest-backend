package com.pinterest.businessservice.service.impl;

import com.pinterest.businessservice.dto.CampaignDto;
import com.pinterest.businessservice.dto.SponsoredPinDto;
import com.pinterest.businessservice.exception.ResourceNotFoundException;
import com.pinterest.businessservice.model.BusinessProfile;
import com.pinterest.businessservice.model.Campaign;
import com.pinterest.businessservice.model.Campaign.CampaignObjective;
import com.pinterest.businessservice.model.Campaign.CampaignStatus;
import com.pinterest.businessservice.model.SponsoredPin;
import com.pinterest.businessservice.repository.BusinessProfileRepository;
import com.pinterest.businessservice.repository.CampaignRepository;
import com.pinterest.businessservice.repository.SponsoredPinRepository;
import com.pinterest.businessservice.service.CampaignService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CampaignServiceImpl implements CampaignService {

    private final CampaignRepository campaignRepository;
    private final BusinessProfileRepository businessProfileRepository;
    private final SponsoredPinRepository sponsoredPinRepository;

    @Override
    @Transactional
    public CampaignDto createCampaign(CampaignDto campaignDto) {
        // Verify business profile exists
        BusinessProfile businessProfile = businessProfileRepository.findById(campaignDto.getBusinessProfileId())
                .orElseThrow(() -> new ResourceNotFoundException("Business profile not found with id: " + campaignDto.getBusinessProfileId()));
        
        // Convert DTO to entity
        Campaign campaign = convertToEntity(campaignDto);
        
        // Set default values if not provided
        if (campaign.getStatus() == null) {
            campaign.setStatus(CampaignStatus.DRAFT);
        }
        
        if (campaign.getImpressions() == null) {
            campaign.setImpressions(0L);
        }
        
        if (campaign.getClicks() == null) {
            campaign.setClicks(0L);
        }
        
        if (campaign.getConversions() == null) {
            campaign.setConversions(0L);
        }
        
        if (campaign.getAmountSpent() == null) {
            campaign.setAmountSpent(BigDecimal.ZERO);
        }
        
        // Set created and updated timestamps
        LocalDateTime now = LocalDateTime.now();
        campaign.setCreatedAt(now);
        campaign.setUpdatedAt(now);
        
        // Save campaign
        Campaign savedCampaign = campaignRepository.save(campaign);
        
        // Convert back to DTO with additional info
        CampaignDto resultDto = convertToDto(savedCampaign);
        resultDto.setBusinessName(businessProfile.getBusinessName());
        resultDto.setBusinessLogoUrl(businessProfile.getLogoUrl());
        
        // Calculate metrics
        calculateMetrics(resultDto);
        
        return resultDto;
    }

    @Override
    public CampaignDto getCampaignById(Long id) {
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found with id: " + id));
        
        CampaignDto campaignDto = convertToDto(campaign);
        
        // Get business profile info
        BusinessProfile businessProfile = businessProfileRepository.findById(campaign.getBusinessProfileId())
                .orElseThrow(() -> new ResourceNotFoundException("Business profile not found with id: " + campaign.getBusinessProfileId()));
        campaignDto.setBusinessName(businessProfile.getBusinessName());
        campaignDto.setBusinessLogoUrl(businessProfile.getLogoUrl());
        
        // Get sponsored pins
        List<SponsoredPin> sponsoredPins = sponsoredPinRepository.findByCampaignId(id);
        campaignDto.setSponsoredPinsCount((long) sponsoredPins.size());
        
        // Convert sponsored pins to DTOs
        List<SponsoredPinDto> sponsoredPinDtos = sponsoredPins.stream()
                .map(this::convertSponsoredPinToDto)
                .collect(Collectors.toList());
        campaignDto.setSponsoredPins(sponsoredPinDtos);
        
        // Calculate metrics
        calculateMetrics(campaignDto);
        
        return campaignDto;
    }

    @Override
    public List<CampaignDto> getCampaignsByBusinessProfileId(Long businessProfileId) {
        // Verify business profile exists
        if (!businessProfileRepository.existsById(businessProfileId)) {
            throw new ResourceNotFoundException("Business profile not found with id: " + businessProfileId);
        }
        
        List<Campaign> campaigns = campaignRepository.findByBusinessProfileId(businessProfileId);
        return campaigns.stream()
                .map(this::convertToDto)
                .peek(this::calculateMetrics)
                .collect(Collectors.toList());
    }

    @Override
    public Page<CampaignDto> getCampaignsByBusinessProfileId(Long businessProfileId, Pageable pageable) {
        // Verify business profile exists
        if (!businessProfileRepository.existsById(businessProfileId)) {
            throw new ResourceNotFoundException("Business profile not found with id: " + businessProfileId);
        }
        
        Page<Campaign> campaignsPage = campaignRepository.findByBusinessProfileId(businessProfileId, pageable);
        return campaignsPage.map(campaign -> {
            CampaignDto dto = convertToDto(campaign);
            calculateMetrics(dto);
            return dto;
        });
    }

    @Override
    @Transactional
    public CampaignDto updateCampaign(Long id, CampaignDto campaignDto) {
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found with id: " + id));
        
        // Update fields
        campaign.setName(campaignDto.getName());
        campaign.setDescription(campaignDto.getDescription());
        
        if (campaignDto.getObjective() != null) {
            campaign.setObjective(campaignDto.getObjective());
        }
        
        if (campaignDto.getStatus() != null) {
            campaign.setStatus(campaignDto.getStatus());
        }
        
        if (campaignDto.getBudget() != null) {
            campaign.setBudget(campaignDto.getBudget());
        }
        
        if (campaignDto.getDailyBudget() != null) {
            campaign.setDailyBudget(campaignDto.getDailyBudget());
        }
        
        if (campaignDto.getStartDate() != null) {
            campaign.setStartDate(campaignDto.getStartDate());
        }
        
        if (campaignDto.getEndDate() != null) {
            campaign.setEndDate(campaignDto.getEndDate());
        }
        
        if (campaignDto.getTargetAudience() != null) {
            campaign.setTargetAudience(campaignDto.getTargetAudience());
        }
        
        campaign.setUpdatedAt(LocalDateTime.now());
        
        Campaign updatedCampaign = campaignRepository.save(campaign);
        return getCampaignById(updatedCampaign.getId()); // Return full DTO with additional info
    }

    @Override
    @Transactional
    public void deleteCampaign(Long id) {
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found with id: " + id));
        
        // Update sponsored pins to remove campaign association
        List<SponsoredPin> sponsoredPins = sponsoredPinRepository.findByCampaignId(id);
        for (SponsoredPin pin : sponsoredPins) {
            pin.setCampaignId(null);
            pin.setUpdatedAt(LocalDateTime.now());
        }
        sponsoredPinRepository.saveAll(sponsoredPins);
        
        // Delete campaign
        campaignRepository.deleteById(id);
    }

    @Override
    public Page<CampaignDto> getCampaignsByStatus(CampaignStatus status, Pageable pageable) {
        Page<Campaign> campaignsPage = campaignRepository.findByStatus(status, pageable);
        return campaignsPage.map(campaign -> {
            CampaignDto dto = convertToDto(campaign);
            calculateMetrics(dto);
            return dto;
        });
    }

    @Override
    public Page<CampaignDto> getCampaignsByObjective(CampaignObjective objective, Pageable pageable) {
        Page<Campaign> campaignsPage = campaignRepository.findByObjective(objective, pageable);
        return campaignsPage.map(campaign -> {
            CampaignDto dto = convertToDto(campaign);
            calculateMetrics(dto);
            return dto;
        });
    }

    @Override
    public List<CampaignDto> getActiveCampaigns() {
        LocalDate today = LocalDate.now();
        List<Campaign> activeCampaigns = campaignRepository.findActiveCampaigns(today);
        return activeCampaigns.stream()
                .map(this::convertToDto)
                .peek(this::calculateMetrics)
                .collect(Collectors.toList());
    }

    @Override
    public Page<CampaignDto> getActiveCampaigns(Pageable pageable) {
        LocalDate today = LocalDate.now();
        Page<Campaign> activeCampaignsPage = campaignRepository.findActiveCampaigns(today, pageable);
        return activeCampaignsPage.map(campaign -> {
            CampaignDto dto = convertToDto(campaign);
            calculateMetrics(dto);
            return dto;
        });
    }

    @Override
    @Transactional
    public CampaignDto updateCampaignStatus(Long id, CampaignStatus status) {
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found with id: " + id));
        
        campaign.setStatus(status);
        campaign.setUpdatedAt(LocalDateTime.now());
        
        Campaign updatedCampaign = campaignRepository.save(campaign);
        return convertToDto(updatedCampaign);
    }

    @Override
    @Transactional
    public void recordImpression(Long id) {
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found with id: " + id));
        
        campaign.setImpressions(campaign.getImpressions() + 1);
        campaign.setUpdatedAt(LocalDateTime.now());
        
        campaignRepository.save(campaign);
    }

    @Override
    @Transactional
    public void recordClick(Long id) {
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found with id: " + id));
        
        campaign.setClicks(campaign.getClicks() + 1);
        campaign.setUpdatedAt(LocalDateTime.now());
        
        campaignRepository.save(campaign);
    }

    @Override
    @Transactional
    public void recordConversion(Long id) {
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found with id: " + id));
        
        campaign.setConversions(campaign.getConversions() + 1);
        campaign.setUpdatedAt(LocalDateTime.now());
        
        campaignRepository.save(campaign);
    }

    @Override
    @Transactional
    public void updateAmountSpent(Long id, BigDecimal amount) {
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found with id: " + id));
        
        campaign.setAmountSpent(campaign.getAmountSpent().add(amount));
        campaign.setUpdatedAt(LocalDateTime.now());
        
        campaignRepository.save(campaign);
    }

    @Override
    public List<CampaignDto> getCampaignsByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Campaign> campaigns = campaignRepository.findByStartDateLessThanEqualAndEndDateGreaterThanEqual(endDate, startDate);
        return campaigns.stream()
                .map(this::convertToDto)
                .peek(this::calculateMetrics)
                .collect(Collectors.toList());
    }

    @Override
    public Page<CampaignDto> searchCampaigns(String keyword, Pageable pageable) {
        Page<Campaign> campaignsPage = campaignRepository.findByNameContainingOrDescriptionContaining(keyword, keyword, pageable);
        return campaignsPage.map(campaign -> {
            CampaignDto dto = convertToDto(campaign);
            calculateMetrics(dto);
            return dto;
        });
    }

    @Override
    public List<CampaignDto> getScheduledCampaigns() {
        LocalDate today = LocalDate.now();
        List<Campaign> scheduledCampaigns = campaignRepository.findScheduledCampaigns(today);
        return scheduledCampaigns.stream()
                .map(this::convertToDto)
                .peek(this::calculateMetrics)
                .collect(Collectors.toList());
    }

    @Override
    public List<CampaignDto> getCampaignsToComplete() {
        LocalDate today = LocalDate.now();
        List<Campaign> campaignsToComplete = campaignRepository.findCampaignsToComplete(today);
        return campaignsToComplete.stream()
                .map(this::convertToDto)
                .peek(this::calculateMetrics)
                .collect(Collectors.toList());
    }

    // Helper methods for entity-DTO conversion
    private Campaign convertToEntity(CampaignDto dto) {
        Campaign entity = new Campaign();
        entity.setId(dto.getId());
        entity.setBusinessProfileId(dto.getBusinessProfileId());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setObjective(dto.getObjective());
        entity.setStatus(dto.getStatus());
        entity.setBudget(dto.getBudget());
        entity.setDailyBudget(dto.getDailyBudget());
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        entity.setTargetAudience(dto.getTargetAudience());
        entity.setImpressions(dto.getImpressions());
        entity.setClicks(dto.getClicks());
        entity.setConversions(dto.getConversions());
        entity.setAmountSpent(dto.getAmountSpent());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());
        return entity;
    }

    private CampaignDto convertToDto(Campaign entity) {
        CampaignDto dto = new CampaignDto();
        dto.setId(entity.getId());
        dto.setBusinessProfileId(entity.getBusinessProfileId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setObjective(entity.getObjective());
        dto.setStatus(entity.getStatus());
        dto.setBudget(entity.getBudget());
        dto.setDailyBudget(entity.getDailyBudget());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        dto.setTargetAudience(entity.getTargetAudience());
        dto.setImpressions(entity.getImpressions());
        dto.setClicks(entity.getClicks());
        dto.setConversions(entity.getConversions());
        dto.setAmountSpent(entity.getAmountSpent());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        
        // Count sponsored pins
        Long sponsoredPinsCount = sponsoredPinRepository.countByCampaignId(entity.getId());
        dto.setSponsoredPinsCount(sponsoredPinsCount);
        
        return dto;
    }

    private SponsoredPinDto convertSponsoredPinToDto(SponsoredPin entity) {
        SponsoredPinDto dto = new SponsoredPinDto();
        dto.setId(entity.getId());
        dto.setBusinessProfileId(entity.getBusinessProfileId());
        dto.setPinId(entity.getPinId());
        dto.setCampaignId(entity.getCampaignId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setTargetUrl(entity.getTargetUrl());
        dto.setStatus(entity.getStatus());
        dto.setBudget(entity.getBudget());
        dto.setBidAmount(entity.getBidAmount());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        dto.setImpressions(entity.getImpressions());
        dto.setClicks(entity.getClicks());
        dto.setSaves(entity.getSaves());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        
        // Calculate metrics
        if (entity.getImpressions() != null && entity.getImpressions() > 0 && entity.getClicks() != null) {
            double ctr = (double) entity.getClicks() / entity.getImpressions() * 100;
            dto.setClickThroughRate(BigDecimal.valueOf(ctr).setScale(2, RoundingMode.HALF_UP));
        } else {
            dto.setClickThroughRate(BigDecimal.ZERO.setScale(2));
        }
        
        if (entity.getClicks() != null && entity.getClicks() > 0 && entity.getBidAmount() != null) {
            BigDecimal cpc = entity.getBidAmount();
            dto.setCostPerClick(cpc.setScale(2, RoundingMode.HALF_UP));
        } else {
            dto.setCostPerClick(BigDecimal.ZERO.setScale(2));
        }
        
        return dto;
    }

    private void calculateMetrics(CampaignDto dto) {
        // Calculate Click-Through Rate (CTR)
        if (dto.getImpressions() != null && dto.getImpressions() > 0 && dto.getClicks() != null) {
            double ctr = (double) dto.getClicks() / dto.getImpressions() * 100;
            dto.setClickThroughRate(BigDecimal.valueOf(ctr).setScale(2, RoundingMode.HALF_UP));
        } else {
            dto.setClickThroughRate(BigDecimal.ZERO.setScale(2));
        }
        
        // Calculate Cost Per Click (CPC)
        if (dto.getClicks() != null && dto.getClicks() > 0 && dto.getAmountSpent() != null) {
            BigDecimal cpc = dto.getAmountSpent().divide(BigDecimal.valueOf(dto.getClicks()), 2, RoundingMode.HALF_UP);
            dto.setCostPerClick(cpc);
        } else {
            dto.setCostPerClick(BigDecimal.ZERO.setScale(2));
        }
        
        // Calculate Conversion Rate
        if (dto.getClicks() != null && dto.getClicks() > 0 && dto.getConversions() != null) {
            double convRate = (double) dto.getConversions() / dto.getClicks() * 100;
            dto.setConversionRate(BigDecimal.valueOf(convRate).setScale(2, RoundingMode.HALF_UP));
        } else {
            dto.setConversionRate(BigDecimal.ZERO.setScale(2));
        }
        
        // Calculate Return on Ad Spend (ROAS)
        // Note: This would typically require revenue data from conversions, which we don't have
        // For now, we'll set a placeholder value
        dto.setReturnOnAdSpend(BigDecimal.ZERO.setScale(2));
    }
}