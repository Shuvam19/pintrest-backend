package com.pinterest.businessservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShowcaseDto {
    
    private Long id;
    private Long businessProfileId;
    private String title;
    private String description;
    private String coverImageUrl;
    private String theme;
    private boolean featured;
    private boolean active;
    private Integer displayOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Additional fields for UI display
    private String businessName;
    private String businessLogoUrl;
    private int itemsCount;
    private List<ShowcaseItemDto> items;
}