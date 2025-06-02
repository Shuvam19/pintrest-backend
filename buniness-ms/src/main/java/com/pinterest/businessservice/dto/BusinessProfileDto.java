package com.pinterest.businessservice.dto;

import com.pinterest.businessservice.model.BusinessProfile;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessProfileDto {
    
    private Long id;
    private Long userId;
    private String businessName;
    private String logoUrl;
    private String description;
    private String websiteUrl;
    private BusinessProfile.BusinessCategory category;
    private String contactEmail;
    private String contactPhone;
    private String address;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private BusinessProfile.VerificationStatus verificationStatus;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Additional fields for UI display
    private int followersCount;
    private int showcasesCount;
    private int pinsCount;
}