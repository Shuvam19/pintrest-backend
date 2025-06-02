package com.pinterest.collaborationservice.dto;

import com.pinterest.collaborationservice.model.BoardCollaboration;
import com.pinterest.collaborationservice.model.Invitation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvitationDto {
    
    private Long id;
    private Long senderId;
    private Long recipientId;
    private Invitation.InvitationType type;
    private Long referenceId;
    private Invitation.InvitationStatus status;
    private String message;
    private BoardCollaboration.PermissionLevel permissionLevel;
    private LocalDateTime respondedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Additional fields for UI display
    private String senderUsername;
    private String senderProfileImageUrl;
    private String recipientUsername;
    private String recipientProfileImageUrl;
    
    // For board collaborations
    private String boardTitle;
    private String boardCoverImageUrl;
}