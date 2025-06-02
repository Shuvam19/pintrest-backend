package com.pinterest.businessservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "campaigns")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Associated business profile ID
    @Column(name = "business_profile_id", nullable = false)
    private Long businessProfileId;
    
    // Campaign name
    @Column(nullable = false)
    private String name;
    
    // Campaign description
    @Column(columnDefinition = "TEXT")
    private String description;
    
    // Campaign objective
    @Enumerated(EnumType.STRING)
    private CampaignObjective objective;
    
    // Campaign status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CampaignStatus status;
    
    // Campaign budget
    @Column(precision = 10, scale = 2)
    private BigDecimal budget;
    
    // Daily budget
    @Column(name = "daily_budget", precision = 10, scale = 2)
    private BigDecimal dailyBudget;
    
    // Start date of the campaign
    @Column(name = "start_date")
    private LocalDateTime startDate;
    
    // End date of the campaign
    @Column(name = "end_date")
    private LocalDateTime endDate;
    
    // Target audience - JSON string with targeting criteria
    @Column(name = "target_audience", columnDefinition = "TEXT")
    private String targetAudience;
    
    // Impressions count
    private Long impressions;
    
    // Clicks count
    private Long clicks;
    
    // Conversions count
    private Long conversions;
    
    // Amount spent
    @Column(name = "amount_spent", precision = 10, scale = 2)
    private BigDecimal amountSpent;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    // Enum for campaign objectives
    public enum CampaignObjective {
        BRAND_AWARENESS,
        TRAFFIC,
        APP_INSTALLS,
        VIDEO_VIEWS,
        CONVERSIONS,
        CATALOG_SALES,
        LEAD_GENERATION
    }
    
    // Enum for campaign status
    public enum CampaignStatus {
        DRAFT,
        SCHEDULED,
        ACTIVE,
        PAUSED,
        COMPLETED,
        ARCHIVED
    }
}