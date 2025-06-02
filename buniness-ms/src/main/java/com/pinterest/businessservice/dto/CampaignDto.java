package com.pinterest.businessservice.dto;

import com.pinterest.businessservice.model.Campaign;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampaignDto {
    
    private Long id;
    private Long businessProfileId;
    private String name;
    private String description;
    private Campaign.CampaignObjective objective;
    private Campaign.CampaignStatus status;
    private BigDecimal budget;
    private BigDecimal dailyBudget;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String targetAudience;
    private Long impressions;
    private Long clicks;
    private Long conversions;
    private BigDecimal amountSpent;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Additional fields for UI display
    private String businessName;
    private String businessLogoUrl;
    private int sponsoredPinsCount;
    private List<SponsoredPinDto> sponsoredPins;
    private double clickThroughRate; // CTR = clicks / impressions
    private double costPerClick; // CPC = amount spent / clicks
    private double conversionRate; // CR = conversions / clicks
    private double returnOnAdSpend; // ROAS = revenue / amount spent
}