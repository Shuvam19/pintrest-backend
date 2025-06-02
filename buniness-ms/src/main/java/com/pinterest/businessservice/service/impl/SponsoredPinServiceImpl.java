package com.pinterest.businessservice.service.impl;

import com.pinterest.businessservice.dto.SponsoredPinDto;
import com.pinterest.businessservice.exception.ResourceNotFoundException;
import com.pinterest.businessservice.model.BusinessProfile;
import com.pinterest.businessservice.model.Campaign;
import com.pinterest.businessservice.model.SponsoredPin;
import com.pinterest.businessservice.model.SponsoredPin.SponsoredStatus;
import com.pinterest.businessservice.repository.BusinessProfileRepository;
import com.pinterest.businessservice.repository.CampaignRepository;
import com.pinterest.businessservice.repository.SponsoredPinRepository;
import com.pinterest.businessservice.service.SponsoredPinService;
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
public class SponsoredPinServiceImpl implements SponsoredPinService {

    private final SponsoredPinRepository sponsoredPinRepository;
    private final BusinessProfileRepository businessProfileRepository;
    private final CampaignRepository campaignRepository;

    @Override
    @Transactional
    public SponsoredPinDto createSponsoredPin(SponsoredPinDto sponsoredPinDto) {
        // Verify business profile exists
        BusinessProfile businessProfile = businessProfileRepository.findById(sponsoredPinDto.getBusinessProfileId())
                .orElseThrow(() -> new ResourceNotFoundException("Business profile not found with id: " + sponsoredPinDto.getBusinessProfileId()));
        
        // Verify campaign exists if provided
        Campaign campaign = null;
        if (sponsoredPinDto.getCampaignId() != null) {
            campaign = campaignRepository.findById(sponsoredPinDto.getCampaignId())
                    .orElseThrow(() -> new ResourceNotFoundException("Campaign not found with id: " + sponsoredPinDto.getCampaignId()));
            
            // Verify campaign belongs to the business profile
            if (!campaign.getBusinessProfileId().equals(businessProfile.getId())) {
                throw new IllegalArgumentException("Campaign does not belong to the specified business profile");
            }
        }
        
        // Convert DTO to entity
        SponsoredPin sponsoredPin = convertToEntity(sponsoredPinDto);
        
        // Set default values if not provided
        if (sponsoredPin.getStatus() == null) {
            sponsoredPin.setStatus(SponsoredStatus.DRAFT);
        }
        
        if (sponsoredPin.getImpressions() == null) {
            sponsoredPin.setImpressions(0L);
        }
        
        if (sponsoredPin.getClicks() == null) {
            sponsoredPin.setClicks(0L);
        }
        
        if (sponsoredPin.getSaves() == null) {
            sponsoredPin.setSaves(0L);
        }
        
        // Set created and updated timestamps
        LocalDateTime now = LocalDateTime.now();
        sponsoredPin.setCreatedAt(now);
        sponsoredPin.setUpdatedAt(now);
        
        // Save sponsored pin
        SponsoredPin savedPin = sponsoredPinRepository.save(sponsoredPin);
        
        // Convert back to DTO with additional info
        SponsoredPinDto resultDto = convertToDto(savedPin);
        resultDto.setBusinessName(businessProfile.getBusinessName());
        resultDto.setBusinessLogoUrl(businessProfile.getLogoUrl());
        
        if (campaign != null) {
            resultDto.setCampaignName(campaign.getName());
        }
        
        // Calculate metrics
        calculateMetrics(resultDto);
        
        return resultDto;
    }

    @Override
    public SponsoredPinDto getSponsoredPinById(Long id) {
        SponsoredPin sponsoredPin = sponsoredPinRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sponsored pin not found with id: " + id));
        
        SponsoredPinDto sponsoredPinDto = convertToDto(sponsoredPin);
        
        // Get business profile info
        BusinessProfile businessProfile = businessProfileRepository.findById(sponsoredPin.getBusinessProfileId())
                .orElseThrow(() -> new ResourceNotFoundException("Business profile not found with id: " + sponsoredPin.getBusinessProfileId()));
        sponsoredPinDto.setBusinessName(businessProfile.getBusinessName());
        sponsoredPinDto.setBusinessLogoUrl(businessProfile.getLogoUrl());
        
        // Get campaign info if available
        if (sponsoredPin.getCampaignId() != null) {
            campaignRepository.findById(sponsoredPin.getCampaignId())
                    .ifPresent(campaign -> sponsoredPinDto.setCampaignName(campaign.getName()));
        }
        
        // Calculate metrics
        calculateMetrics(sponsoredPinDto);
        
        return sponsoredPinDto;
    }

    @Override
    public List<SponsoredPinDto> getSponsoredPinsByBusinessProfileId(Long businessProfileId) {
        // Verify business profile exists
        if (!businessProfileRepository.existsById(businessProfileId)) {
            throw new ResourceNotFoundException("Business profile not found with id: " + businessProfileId);
        }
        
        List<SponsoredPin> sponsoredPins = sponsoredPinRepository.findByBusinessProfileId(businessProfileId);
        return sponsoredPins.stream()
                .map(this::convertToDto)
                .peek(this::calculateMetrics)
                .collect(Collectors.toList());
    }

    @Override
    public Page<SponsoredPinDto> getSponsoredPinsByBusinessProfileId(Long businessProfileId, Pageable pageable) {
        // Verify business profile exists
        if (!businessProfileRepository.existsById(businessProfileId)) {
            throw new ResourceNotFoundException("Business profile not found with id: " + businessProfileId);
        }
        
        Page<SponsoredPin> sponsoredPinsPage = sponsoredPinRepository.findByBusinessProfileId(businessProfileId, pageable);
        return sponsoredPinsPage.map(pin -> {
            SponsoredPinDto dto = convertToDto(pin);
            calculateMetrics(dto);
            return dto;
        });
    }

    @Override
    public List<SponsoredPinDto> getSponsoredPinsByCampaignId(Long campaignId) {
        // Verify campaign exists
        if (!campaignRepository.existsById(campaignId)) {
            throw new ResourceNotFoundException("Campaign not found with id: " + campaignId);
        }
        
        List<SponsoredPin> sponsoredPins = sponsoredPinRepository.findByCampaignId(campaignId);
        return sponsoredPins.stream()
                .map(this::convertToDto)
                .peek(this::calculateMetrics)
                .collect(Collectors.toList());
    }

    @Override
    public Page<SponsoredPinDto> getSponsoredPinsByCampaignId(Long campaignId, Pageable pageable) {
        // Verify campaign exists
        if (!campaignRepository.existsById(campaignId)) {
            throw new ResourceNotFoundException("Campaign not found with id: " + campaignId);
        }
        
        Page<SponsoredPin> sponsoredPinsPage = sponsoredPinRepository.findByCampaignId(campaignId, pageable);
        return sponsoredPinsPage.map(pin -> {
            SponsoredPinDto dto = convertToDto(pin);
            calculateMetrics(dto);
            return dto;
        });
    }

    @Override
    @Transactional
    public SponsoredPinDto updateSponsoredPin(Long id, SponsoredPinDto sponsoredPinDto) {
        SponsoredPin sponsoredPin = sponsoredPinRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sponsored pin not found with id: " + id));
        
        // Verify campaign exists if provided and changed
        if (sponsoredPinDto.getCampaignId() != null && 
                !sponsoredPinDto.getCampaignId().equals(sponsoredPin.getCampaignId())) {
            
            Campaign campaign = campaignRepository.findById(sponsoredPinDto.getCampaignId())
                    .orElseThrow(() -> new ResourceNotFoundException("Campaign not found with id: " + sponsoredPinDto.getCampaignId()));
            
            // Verify campaign belongs to the business profile
            if (!campaign.getBusinessProfileId().equals(sponsoredPin.getBusinessProfileId())) {
                throw new IllegalArgumentException("Campaign does not belong to the business profile of this sponsored pin");
            }
            
            sponsoredPin.setCampaignId(sponsoredPinDto.getCampaignId());
        }
        
        // Update fields
        sponsoredPin.setTitle(sponsoredPinDto.getTitle());
        sponsoredPin.setDescription(sponsoredPinDto.getDescription());
        sponsoredPin.setTargetUrl(sponsoredPinDto.getTargetUrl());
        
        if (sponsoredPinDto.getStatus() != null) {
            sponsoredPin.setStatus(sponsoredPinDto.getStatus());
        }
        
        if (sponsoredPinDto.getBudget() != null) {
            sponsoredPin.setBudget(sponsoredPinDto.getBudget());
        }
        
        if (sponsoredPinDto.getBidAmount() != null) {
            sponsoredPin.setBidAmount(sponsoredPinDto.getBidAmount());
        }
        
        if (sponsoredPinDto.getStartDate() != null) {
            sponsoredPin.setStartDate(sponsoredPinDto.getStartDate());
        }
        
        if (sponsoredPinDto.getEndDate() != null) {
            sponsoredPin.setEndDate(sponsoredPinDto.getEndDate());
        }
        
        sponsoredPin.setUpdatedAt(LocalDateTime.now());
        
        SponsoredPin updatedPin = sponsoredPinRepository.save(sponsoredPin);
        return getSponsoredPinById(updatedPin.getId()); // Return full DTO with additional info
    }

    @Override
    @Transactional
    public void deleteSponsoredPin(Long id) {
        if (!sponsoredPinRepository.existsById(id)) {
            throw new ResourceNotFoundException("Sponsored pin not found with id: " + id);
        }
        
        sponsoredPinRepository.deleteById(id);
    }

    @Override
    public Page<SponsoredPinDto> getSponsoredPinsByStatus(SponsoredStatus status, Pageable pageable) {
        Page<SponsoredPin> sponsoredPinsPage = sponsoredPinRepository.findByStatus(status, pageable);
        return sponsoredPinsPage.map(pin -> {
            SponsoredPinDto dto = convertToDto(pin);
            calculateMetrics(dto);
            return dto;
        });
    }

    @Override
    public List<SponsoredPinDto> getActiveSponsoredPins() {
        LocalDate today = LocalDate.now();
        List<SponsoredPin> activePins = sponsoredPinRepository.findActiveSponsoredPins(today);
        return activePins.stream()
                .map(this::convertToDto)
                .peek(this::calculateMetrics)
                .collect(Collectors.toList());
    }

    @Override
    public Page<SponsoredPinDto> getActiveSponsoredPins(Pageable pageable) {
        LocalDate today = LocalDate.now();
        Page<SponsoredPin> activePinsPage = sponsoredPinRepository.findActiveSponsoredPins(today, pageable);
        return activePinsPage.map(pin -> {
            SponsoredPinDto dto = convertToDto(pin);
            calculateMetrics(dto);
            return dto;
        });
    }

    @Override
    @Transactional
    public SponsoredPinDto updateSponsoredPinStatus(Long id, SponsoredStatus status) {
        SponsoredPin sponsoredPin = sponsoredPinRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sponsored pin not found with id: " + id));
        
        sponsoredPin.setStatus(status);
        sponsoredPin.setUpdatedAt(LocalDateTime.now());
        
        SponsoredPin updatedPin = sponsoredPinRepository.save(sponsoredPin);
        return convertToDto(updatedPin);
    }

    @Override
    @Transactional
    public void recordImpression(Long id) {
        SponsoredPin sponsoredPin = sponsoredPinRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sponsored pin not found with id: " + id));
        
        sponsoredPin.setImpressions(sponsoredPin.getImpressions() + 1);
        sponsoredPin.setUpdatedAt(LocalDateTime.now());
        
        sponsoredPinRepository.save(sponsoredPin);
        
        // Update campaign impressions if associated with a campaign
        if (sponsoredPin.getCampaignId() != null) {
            Campaign campaign = campaignRepository.findById(sponsoredPin.getCampaignId())
                    .orElseThrow(() -> new ResourceNotFoundException("Campaign not found with id: " + sponsoredPin.getCampaignId()));
            
            campaign.setImpressions(campaign.getImpressions() + 1);
            campaign.setUpdatedAt(LocalDateTime.now());
            
            campaignRepository.save(campaign);
        }
    }

    @Override
    @Transactional
    public void recordClick(Long id) {
        SponsoredPin sponsoredPin = sponsoredPinRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sponsored pin not found with id: " + id));
        
        sponsoredPin.setClicks(sponsoredPin.getClicks() + 1);
        sponsoredPin.setUpdatedAt(LocalDateTime.now());
        
        sponsoredPinRepository.save(sponsoredPin);
        
        // Update campaign clicks if associated with a campaign
        if (sponsoredPin.getCampaignId() != null) {
            Campaign campaign = campaignRepository.findById(sponsoredPin.getCampaignId())
                    .orElseThrow(() -> new ResourceNotFoundException("Campaign not found with id: " + sponsoredPin.getCampaignId()));
            
            campaign.setClicks(campaign.getClicks() + 1);
            campaign.setUpdatedAt(LocalDateTime.now());
            
            campaignRepository.save(campaign);
        }
    }

    @Override
    @Transactional
    public void recordSave(Long id) {
        SponsoredPin sponsoredPin = sponsoredPinRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sponsored pin not found with id: " + id));
        
        sponsoredPin.setSaves(sponsoredPin.getSaves() + 1);
        sponsoredPin.setUpdatedAt(LocalDateTime.now());
        
        sponsoredPinRepository.save(sponsoredPin);
    }

    @Override
    public List<SponsoredPinDto> getSponsoredPinsByDateRange(LocalDate startDate, LocalDate endDate) {
        List<SponsoredPin> sponsoredPins = sponsoredPinRepository.findByStartDateLessThanEqualAndEndDateGreaterThanEqual(endDate, startDate);
        return sponsoredPins.stream()
                .map(this::convertToDto)
                .peek(this::calculateMetrics)
                .collect(Collectors.toList());
    }

    @Override
    public Page<SponsoredPinDto> searchSponsoredPins(String keyword, Pageable pageable) {
        Page<SponsoredPin> sponsoredPinsPage = sponsoredPinRepository.findByTitleContainingOrDescriptionContaining(keyword, keyword, pageable);
        return sponsoredPinsPage.map(pin -> {
            SponsoredPinDto dto = convertToDto(pin);
            calculateMetrics(dto);
            return dto;
        });
    }

    // Helper methods for entity-DTO conversion
    private SponsoredPin convertToEntity(SponsoredPinDto dto) {
        SponsoredPin entity = new SponsoredPin();
        entity.setId(dto.getId());
        entity.setBusinessProfileId(dto.getBusinessProfileId());
        entity.setPinId(dto.getPinId());
        entity.setCampaignId(dto.getCampaignId());
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setTargetUrl(dto.getTargetUrl());
        entity.setStatus(dto.getStatus());
        entity.setBudget(dto.getBudget());
        entity.setBidAmount(dto.getBidAmount());
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        entity.setImpressions(dto.getImpressions());
        entity.setClicks(dto.getClicks());
        entity.setSaves(dto.getSaves());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());
        return entity;
    }

    private SponsoredPinDto convertToDto(SponsoredPin entity) {
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
        return dto;
    }

    private void calculateMetrics(SponsoredPinDto dto) {
        // Calculate Click-Through Rate (CTR)
        if (dto.getImpressions() != null && dto.getImpressions() > 0 && dto.getClicks() != null) {
            double ctr = (double) dto.getClicks() / dto.getImpressions() * 100;
            dto.setClickThroughRate(BigDecimal.valueOf(ctr).setScale(2, RoundingMode.HALF_UP));
        } else {
            dto.setClickThroughRate(BigDecimal.ZERO.setScale(2));
        }
        
        // Calculate Cost Per Click (CPC)
        if (dto.getClicks() != null && dto.getClicks() > 0 && dto.getBidAmount() != null) {
            BigDecimal cpc = dto.getBidAmount();
            dto.setCostPerClick(cpc.setScale(2, RoundingMode.HALF_UP));
        } else {
            dto.setCostPerClick(BigDecimal.ZERO.setScale(2));
        }
    }
}