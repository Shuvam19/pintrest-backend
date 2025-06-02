package com.pinterest.collaborationservice.dto;

import com.pinterest.collaborationservice.model.BoardCollaboration;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardCollaborationDto {
    
    private Long id;
    private Long boardId;
    private Long userId;
    private Long invitedBy;
    private BoardCollaboration.CollaborationStatus status;
    private BoardCollaboration.PermissionLevel permissionLevel;
    private String invitationMessage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Additional fields for UI display
    private String boardTitle;
    private String boardCoverImageUrl;
    private String userUsername;
    private String userProfileImageUrl;
    private String inviterUsername;
    private String inviterProfileImageUrl;
}