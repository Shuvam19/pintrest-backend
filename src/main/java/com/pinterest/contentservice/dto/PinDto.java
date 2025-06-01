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
public class PinDto {
    
    private Long id;
    
    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title cannot exceed 100 characters")
    private String title;
    
    private String description;
    
    @NotBlank(message = "Image URL is required")
    private String imageUrl;
    
    private String videoUrl;
    
    private String sourceUrl;
    
    private String attribution;
    
    private String keywords;
    
    private boolean isPrivate;
    
    private boolean isDraft;
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    private Long boardId;
    
    private String boardTitle;
    
    private String createdAt;
    
    private String updatedAt;
}