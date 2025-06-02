package com.pinterest.businessservice.service.impl;

import com.pinterest.businessservice.dto.BusinessProfileDto;
import com.pinterest.businessservice.exception.ResourceNotFoundException;
import com.pinterest.businessservice.model.BusinessProfile;
import com.pinterest.businessservice.repository.BusinessProfileRepository;
import com.pinterest.businessservice.service.BusinessProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BusinessProfileServiceImpl implements BusinessProfileService {

    private final BusinessProfileRepository businessProfileRepository;

    @Override
    public BusinessProfileDto createBusinessProfile(BusinessProfileDto businessProfileDto) {
        // Convert DTO to entity
        BusinessProfile businessProfile = mapToEntity(businessProfileDto);
        
        // Set default values if not provided
        if (businessProfile.getVerificationStatus() == null) {
            businessProfile.setVerificationStatus(BusinessProfile.VerificationStatus.PENDING);
        }
        businessProfile.setActive(true);
        
        // Save entity
        BusinessProfile savedBusinessProfile = businessProfileRepository.save(businessProfile);
        
        // Convert entity to DTO and return
        return mapToDto(savedBusinessProfile);
    }

    @Override
    public BusinessProfileDto getBusinessProfileById(Long id) {
        BusinessProfile businessProfile = businessProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Business profile not found with id: " + id));
        return mapToDto(businessProfile);
    }

    @Override
    public BusinessProfileDto getBusinessProfileByUserId(Long userId) {
        BusinessProfile businessProfile = businessProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Business profile not found for user id: " + userId));
        return mapToDto(businessProfile);
    }

    @Override
    public BusinessProfileDto updateBusinessProfile(Long id, BusinessProfileDto businessProfileDto) {
        // Check if business profile exists
        BusinessProfile businessProfile = businessProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Business profile not found with id: " + id));
        
        // Update fields
        businessProfile.setBusinessName(businessProfileDto.getBusinessName());
        businessProfile.setLogoUrl(businessProfileDto.getLogoUrl());
        businessProfile.setDescription(businessProfileDto.getDescription());
        businessProfile.setWebsiteUrl(businessProfileDto.getWebsiteUrl());
        businessProfile.setCategory(businessProfileDto.getCategory());
        businessProfile.setContactEmail(businessProfileDto.getContactEmail());
        businessProfile.setContactPhone(businessProfileDto.getContactPhone());
        businessProfile.setAddress(businessProfileDto.getAddress());
        businessProfile.setCity(businessProfileDto.getCity());
        businessProfile.setState(businessProfileDto.getState());
        businessProfile.setCountry(businessProfileDto.getCountry());
        businessProfile.setPostalCode(businessProfileDto.getPostalCode());
        
        // Save updated entity
        BusinessProfile updatedBusinessProfile = businessProfileRepository.save(businessProfile);
        
        // Convert entity to DTO and return
        return mapToDto(updatedBusinessProfile);
    }

    @Override
    public void deleteBusinessProfile(Long id) {
        // Check if business profile exists
        BusinessProfile businessProfile = businessProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Business profile not found with id: " + id));
        
        // Delete business profile
        businessProfileRepository.delete(businessProfile);
    }

    @Override
    public Page<BusinessProfileDto> getAllBusinessProfiles(Pageable pageable) {
        Page<BusinessProfile> businessProfilesPage = businessProfileRepository.findAll(pageable);
        List<BusinessProfileDto> businessProfileDtos = businessProfilesPage.getContent().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        
        return new PageImpl<>(businessProfileDtos, pageable, businessProfilesPage.getTotalElements());
    }

    @Override
    public Page<BusinessProfileDto> getVerifiedBusinessProfiles(Pageable pageable) {
        List<BusinessProfile> verifiedProfiles = businessProfileRepository.findByVerificationStatus(BusinessProfile.VerificationStatus.VERIFIED);
        List<BusinessProfileDto> businessProfileDtos = verifiedProfiles.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), businessProfileDtos.size());
        List<BusinessProfileDto> pageContent = businessProfileDtos.subList(start, end);
        
        return new PageImpl<>(pageContent, pageable, businessProfileDtos.size());
    }

    @Override
    public Page<BusinessProfileDto> searchBusinessProfiles(String keyword, Pageable pageable) {
        List<BusinessProfile> searchResults = businessProfileRepository.searchByBusinessName(keyword);
        List<BusinessProfileDto> businessProfileDtos = searchResults.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), businessProfileDtos.size());
        List<BusinessProfileDto> pageContent = businessProfileDtos.subList(start, end);
        
        return new PageImpl<>(pageContent, pageable, businessProfileDtos.size());
    }

    @Override
    public Page<BusinessProfileDto> getBusinessProfilesByCategory(BusinessProfile.BusinessCategory category, Pageable pageable) {
        List<BusinessProfile> categoryProfiles = businessProfileRepository.findByCategory(category);
        List<BusinessProfileDto> businessProfileDtos = categoryProfiles.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), businessProfileDtos.size());
        List<BusinessProfileDto> pageContent = businessProfileDtos.subList(start, end);
        
        return new PageImpl<>(pageContent, pageable, businessProfileDtos.size());
    }

    @Override
    public BusinessProfileDto updateVerificationStatus(Long id, BusinessProfile.VerificationStatus status) {
        // Check if business profile exists
        BusinessProfile businessProfile = businessProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Business profile not found with id: " + id));
        
        // Update verification status
        businessProfile.setVerificationStatus(status);
        
        // Save updated entity
        BusinessProfile updatedBusinessProfile = businessProfileRepository.save(businessProfile);
        
        // Convert entity to DTO and return
        return mapToDto(updatedBusinessProfile);
    }

    @Override
    public BusinessProfileDto toggleActiveStatus(Long id) {
        // Check if business profile exists
        BusinessProfile businessProfile = businessProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Business profile not found with id: " + id));
        
        // Toggle active status
        businessProfile.setActive(!businessProfile.isActive());
        
        // Save updated entity
        BusinessProfile updatedBusinessProfile = businessProfileRepository.save(businessProfile);
        
        // Convert entity to DTO and return
        return mapToDto(updatedBusinessProfile);
    }
    
    // Helper method to convert Entity to DTO
    private BusinessProfileDto mapToDto(BusinessProfile businessProfile) {
        return BusinessProfileDto.builder()
                .id(businessProfile.getId())
                .userId(businessProfile.getUserId())
                .businessName(businessProfile.getBusinessName())
                .logoUrl(businessProfile.getLogoUrl())
                .description(businessProfile.getDescription())
                .websiteUrl(businessProfile.getWebsiteUrl())
                .category(businessProfile.getCategory())
                .contactEmail(businessProfile.getContactEmail())
                .contactPhone(businessProfile.getContactPhone())
                .address(businessProfile.getAddress())
                .city(businessProfile.getCity())
                .state(businessProfile.getState())
                .country(businessProfile.getCountry())
                .postalCode(businessProfile.getPostalCode())
                .verificationStatus(businessProfile.getVerificationStatus())
                .active(businessProfile.isActive())
                .createdAt(businessProfile.getCreatedAt())
                .updatedAt(businessProfile.getUpdatedAt())
                // Additional fields would be populated from other services
                .build();
    }
    
    // Helper method to convert DTO to Entity
    private BusinessProfile mapToEntity(BusinessProfileDto businessProfileDto) {
        return BusinessProfile.builder()
                .id(businessProfileDto.getId())
                .userId(businessProfileDto.getUserId())
                .businessName(businessProfileDto.getBusinessName())
                .logoUrl(businessProfileDto.getLogoUrl())
                .description(businessProfileDto.getDescription())
                .websiteUrl(businessProfileDto.getWebsiteUrl())
                .category(businessProfileDto.getCategory())
                .contactEmail(businessProfileDto.getContactEmail())
                .contactPhone(businessProfileDto.getContactPhone())
                .address(businessProfileDto.getAddress())
                .city(businessProfileDto.getCity())
                .state(businessProfileDto.getState())
                .country(businessProfileDto.getCountry())
                .postalCode(businessProfileDto.getPostalCode())
                .verificationStatus(businessProfileDto.getVerificationStatus())
                .active(businessProfileDto.isActive())
                .build();
    }
}