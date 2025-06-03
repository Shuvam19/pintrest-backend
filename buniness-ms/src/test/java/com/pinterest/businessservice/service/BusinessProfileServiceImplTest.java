package com.pinterest.businessservice.service;

import com.pinterest.businessservice.dto.BusinessProfileDto;
import com.pinterest.businessservice.exception.ResourceNotFoundException;
import com.pinterest.businessservice.model.BusinessProfile;
import com.pinterest.businessservice.repository.BusinessProfileRepository;
import com.pinterest.businessservice.service.impl.BusinessProfileServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BusinessProfileServiceImplTest {

    @Mock
    private BusinessProfileRepository businessProfileRepository;

    @InjectMocks
    private BusinessProfileServiceImpl businessProfileService;

    private BusinessProfile businessProfile;
    private BusinessProfileDto businessProfileDto;

    @BeforeEach
    void setUp() {
        businessProfile = BusinessProfile.builder()
                .id(1L)
                .userId(101L)
                .businessName("Test Business")
                .description("Test Description")
                .websiteUrl("https://testbusiness.com")
                .category(BusinessProfile.BusinessCategory.RETAIL)
                .contactEmail("contact@testbusiness.com")
                .verificationStatus(BusinessProfile.VerificationStatus.VERIFIED)
                .active(true)
                .build();

        businessProfileDto = BusinessProfileDto.builder()
                .id(1L)
                .userId(101L)
                .businessName("Test Business")
                .description("Test Description")
                .websiteUrl("https://testbusiness.com")
                .category(BusinessProfile.BusinessCategory.RETAIL)
                .contactEmail("contact@testbusiness.com")
                .verificationStatus(BusinessProfile.VerificationStatus.VERIFIED)
                .active(true)
                .build();
    }

    @Test
    @DisplayName("Should create business profile successfully")
    void shouldCreateBusinessProfile() {
        when(businessProfileRepository.save(any(BusinessProfile.class)))
                .thenReturn(businessProfile);

        BusinessProfileDto savedDto = businessProfileService.createBusinessProfile(businessProfileDto);

        assertThat(savedDto).isNotNull();
        assertThat(savedDto.getId()).isEqualTo(1L);
        assertThat(savedDto.getBusinessName()).isEqualTo("Test Business");
        verify(businessProfileRepository, times(1)).save(any(BusinessProfile.class));
    }

    @Test
    @DisplayName("Should get business profile by ID")
    void shouldGetBusinessProfileById() {
        when(businessProfileRepository.findById(1L))
                .thenReturn(Optional.of(businessProfile));

        BusinessProfileDto foundDto = businessProfileService.getBusinessProfileById(1L);

        assertThat(foundDto).isNotNull();
        assertThat(foundDto.getId()).isEqualTo(1L);
        verify(businessProfileRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when business profile not found by ID")
    void shouldThrowExceptionWhenBusinessProfileNotFoundById() {
        when(businessProfileRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            businessProfileService.getBusinessProfileById(999L);
        });

        verify(businessProfileRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should get business profile by user ID")
    void shouldGetBusinessProfileByUserId() {
        when(businessProfileRepository.findByUserId(101L))
                .thenReturn(Optional.of(businessProfile));

        BusinessProfileDto foundDto = businessProfileService.getBusinessProfileByUserId(101L);

        assertThat(foundDto).isNotNull();
        assertThat(foundDto.getUserId()).isEqualTo(101L);
        verify(businessProfileRepository, times(1)).findByUserId(101L);
    }

    @Test
    @DisplayName("Should update business profile successfully")
    void shouldUpdateBusinessProfile() {
        when(businessProfileRepository.findById(1L))
                .thenReturn(Optional.of(businessProfile));
        when(businessProfileRepository.save(any(BusinessProfile.class)))
                .thenReturn(businessProfile);

        businessProfileDto.setBusinessName("Updated Business Name");
        BusinessProfileDto updatedDto = businessProfileService.updateBusinessProfile(1L, businessProfileDto);

        assertThat(updatedDto).isNotNull();
        verify(businessProfileRepository, times(1)).findById(1L);
        verify(businessProfileRepository, times(1)).save(any(BusinessProfile.class));
    }

    @Test
    @DisplayName("Should delete business profile successfully")
    void shouldDeleteBusinessProfile() {
        when(businessProfileRepository.findById(1L))
                .thenReturn(Optional.of(businessProfile));
        doNothing().when(businessProfileRepository).delete(businessProfile);

        businessProfileService.deleteBusinessProfile(1L);

        verify(businessProfileRepository, times(1)).findById(1L);
        verify(businessProfileRepository, times(1)).delete(businessProfile);
    }

    @Test
    @DisplayName("Should get all business profiles with pagination")
    void shouldGetAllBusinessProfiles() {
        Page<BusinessProfile> page = new PageImpl<>(List.of(businessProfile));
        when(businessProfileRepository.findAll(any(Pageable.class)))
                .thenReturn(page);

        Page<BusinessProfileDto> resultPage = businessProfileService.getAllBusinessProfiles(Pageable.unpaged());

        assertThat(resultPage).isNotNull();
        assertThat(resultPage.getContent()).hasSize(1);
        verify(businessProfileRepository, times(1)).findAll(any(Pageable.class));
    }
}