package com.pinterest.businessservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShowcaseItemDto {
    
    private Long id;
    private Long showcaseId;
    private Long pinId;
    private String description;
    private Integer displayOrder;
    private boolean featured;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Additional fields for UI display
    private String pinTitle;
    private String pinImageUrl;
    private String pinDescription;
}