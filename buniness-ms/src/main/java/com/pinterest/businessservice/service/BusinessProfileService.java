package com.pinterest.businessservice.service;

import com.pinterest.businessservice.dto.BusinessProfileDto;
import com.pinterest.businessservice.model.BusinessProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BusinessProfileService {
    
    BusinessProfileDto createBusinessProfile(BusinessProfileDto businessProfileDto);
    
    BusinessProfileDto getBusinessProfileById(Long id);
    
    BusinessProfileDto getBusinessProfileByUserId(Long userId);
    
    BusinessProfileDto updateBusinessProfile(Long id, BusinessProfileDto businessProfileDto);
    
    void deleteBusinessProfile(Long id);
    
    Page<BusinessProfileDto> getAllBusinessProfiles(Pageable pageable);
    
    Page<BusinessProfileDto> getVerifiedBusinessProfiles(Pageable pageable);
    
    Page<BusinessProfileDto> searchBusinessProfiles(String keyword, Pageable pageable);
    
    Page<BusinessProfileDto> getBusinessProfilesByCategory(BusinessProfile.BusinessCategory category, Pageable pageable);
    
    BusinessProfileDto updateVerificationStatus(Long id, BusinessProfile.VerificationStatus status);
    
    BusinessProfileDto toggleActiveStatus(Long id);
}