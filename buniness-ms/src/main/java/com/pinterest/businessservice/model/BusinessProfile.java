package com.pinterest.businessservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "business_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Associated user ID
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;
    
    // Business name
    @Column(name = "business_name", nullable = false)
    private String businessName;
    
    // Business logo URL
    @Column(name = "logo_url")
    private String logoUrl;
    
    // Business description
    @Column(columnDefinition = "TEXT")
    private String description;
    
    // Business website URL
    private String websiteUrl;
    
    // Business category
    @Enumerated(EnumType.STRING)
    private BusinessCategory category;
    
    // Business contact email
    private String contactEmail;
    
    // Business contact phone
    private String contactPhone;
    
    // Business address
    private String address;
    
    // Business city
    private String city;
    
    // Business state/province
    private String state;
    
    // Business country
    private String country;
    
    // Business postal code
    @Column(name = "postal_code")
    private String postalCode;
    
    // Verification status
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VerificationStatus verificationStatus;
    
    // Is the business profile active
    @Column(nullable = false)
    private boolean active;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    // Enum for business categories
    public enum BusinessCategory {
        RETAIL,
        FOOD_AND_BEVERAGE,
        FASHION,
        BEAUTY,
        HOME_DECOR,
        TECHNOLOGY,
        HEALTH_AND_WELLNESS,
        TRAVEL,
        EDUCATION,
        ENTERTAINMENT,
        FINANCE,
        AUTOMOTIVE,
        REAL_ESTATE,
        ARTS_AND_CRAFTS,
        OTHER
    }
    
    // Enum for verification status
    public enum VerificationStatus {
        PENDING,
        VERIFIED,
        REJECTED
    }
}