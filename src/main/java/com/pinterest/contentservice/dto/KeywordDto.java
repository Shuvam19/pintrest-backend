package com.pinterest.contentservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KeywordDto {
    
    private Long id;
    
    @NotBlank(message = "Keyword name is required")
    @Size(max = 50, message = "Keyword name cannot exceed 50 characters")
    private String name;
    
    private int pinCount;
    
    private String createdAt;
    
    private String updatedAt;
}