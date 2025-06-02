package com.pinterest.collaborationservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConnectionRequest {
    
    @NotNull(message = "Follower ID is required")
    private Long followerId;
    
    @NotNull(message = "Following ID is required")
    private Long followingId;
    
    private String note;
    
    private boolean notificationsEnabled = true;
}