package com.pinterest.contentservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardDto {
    
    private Long id;
    
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
    
    private List<PinDto> pins = new ArrayList<>();
    
    private int pinCount;
    
    private String createdAt;
    
    private String updatedAt;
}