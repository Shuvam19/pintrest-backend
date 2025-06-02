package com.pinterest.businessservice.dto;

import com.pinterest.businessservice.model.SponsoredPin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SponsoredPinDto {
    
    private Long id;
    private Long businessProfileId;
    private Long pinId;
    private Long campaignId;
    private String title;
    private String description;
    private String targetUrl;
    private SponsoredPin.SponsoredStatus status;
    private BigDecimal budget;
    private BigDecimal bidAmount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Long impressions;
    private Long clicks;
    private Long saves;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Additional fields for UI display
    private String businessName;
    private String businessLogoUrl;
    private String pinImageUrl;
    private String campaignName;
    private double clickThroughRate; // CTR = clicks / impressions
    private double costPerClick; // CPC = amount spent / clicks
}