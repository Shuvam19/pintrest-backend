package com.pinterest.businessservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pinterest.businessservice.dto.BusinessProfileDto;
import com.pinterest.businessservice.model.BusinessProfile;
import com.pinterest.businessservice.service.BusinessProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BusinessProfileController.class)
public class BusinessProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BusinessProfileService businessProfileService;

    @Autowired
    private ObjectMapper objectMapper;

    private BusinessProfileDto businessProfileDto;

    @BeforeEach
    void setUp() {
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
    void shouldCreateBusinessProfile() throws Exception {
        when(businessProfileService.createBusinessProfile(any(BusinessProfileDto.class)))
                .thenReturn(businessProfileDto);

        mockMvc.perform(post("/api/business-profiles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(businessProfileDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.businessName").value("Test Business"));
    }

    @Test
    @DisplayName("Should get business profile by ID")
    void shouldGetBusinessProfileById() throws Exception {
        when(businessProfileService.getBusinessProfileById(1L))
                .thenReturn(businessProfileDto);

        mockMvc.perform(get("/api/business-profiles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.businessName").value("Test Business"));
    }

    @Test
    @DisplayName("Should get business profile by user ID")
    void shouldGetBusinessProfileByUserId() throws Exception {
        when(businessProfileService.getBusinessProfileByUserId(101L))
                .thenReturn(businessProfileDto);

        mockMvc.perform(get("/api/business-profiles/user/101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.userId").value(101L));
    }

    @Test
    @DisplayName("Should get all business profiles with pagination")
    void shouldGetAllBusinessProfiles() throws Exception {
        Page<BusinessProfileDto> page = new PageImpl<>(List.of(businessProfileDto));
        when(businessProfileService.getAllBusinessProfiles(any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/business-profiles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].id").value(1L));
    }

    @Test
    @DisplayName("Should get business profiles by category")
    void shouldGetBusinessProfilesByCategory() throws Exception {
        Page<BusinessProfileDto> page = new PageImpl<>(List.of(businessProfileDto));
        when(businessProfileService.getBusinessProfilesByCategory(
                eq(BusinessProfile.BusinessCategory.RETAIL), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/business-profiles/category/RETAIL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].category").value("RETAIL"));
    }
}