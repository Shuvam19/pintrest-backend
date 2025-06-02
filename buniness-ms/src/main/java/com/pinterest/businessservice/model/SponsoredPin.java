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
@Table(name = "sponsored_pins")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SponsoredPin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Associated business profile ID
    @Column(name = "business_profile_id", nullable = false)
    private Long businessProfileId;
    
    // Pin ID
    @Column(name = "pin_id", nullable = false)
    private Long pinId;
    
    // Campaign ID
    @Column(name = "campaign_id")
    private Long campaignId;
    
    // Sponsored pin title
    @Column(nullable = false)
    private String title;
    
    // Sponsored pin description
    @Column(columnDefinition = "TEXT")
    private String description;
    
    // Target URL
    @Column(name = "target_url")
    private String targetUrl;
    
    // Status of the sponsored pin
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SponsoredStatus status;
    
    // Budget for the sponsored pin
    @Column(precision = 10, scale = 2)
    private BigDecimal budget;
    
    // Bid amount
    @Column(name = "bid_amount", precision = 10, scale = 2)
    private BigDecimal bidAmount;
    
    // Start date of the sponsorship
    @Column(name = "start_date")
    private LocalDateTime startDate;
    
    // End date of the sponsorship
    @Column(name = "end_date")
    private LocalDateTime endDate;
    
    // Impressions count
    private Long impressions;
    
    // Clicks count
    private Long clicks;
    
    // Saves count
    private Long saves;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    // Enum for sponsored pin status
    public enum SponsoredStatus {
        DRAFT,
        PENDING_REVIEW,
        ACTIVE,
        PAUSED,
        REJECTED,
        COMPLETED,
        ARCHIVED
    }
}