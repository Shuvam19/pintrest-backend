package com.pinterest.collaborationservice.dto;

import com.pinterest.collaborationservice.model.UserConnection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserConnectionDto {
    
    private Long id;
    private Long followerId;
    private Long followingId;
    private UserConnection.ConnectionStatus status;
    private String note;
    private boolean notificationsEnabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Additional fields for UI display
    private String followerUsername;
    private String followerProfileImageUrl;
    private String followingUsername;
    private String followingProfileImageUrl;
    private boolean isMutual;
}