package com.pinterest.contentservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardRequest {
    
    @NotBlank(message = "Board title is required")
    @Size(max = 100, message = "Title cannot exceed 100 characters")
    private String title;
    
    private String description;
    
    private String coverImageUrl;
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    private boolean isPrivate;
    
    private String category;
    
    private Integer displayOrder;
    
    private boolean isCollaborative;
}