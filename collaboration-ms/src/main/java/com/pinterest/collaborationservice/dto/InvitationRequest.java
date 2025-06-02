package com.pinterest.collaborationservice.dto;

import com.pinterest.collaborationservice.model.BoardCollaboration;
import com.pinterest.collaborationservice.model.Invitation;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvitationRequest {
    
    @NotNull(message = "Sender ID is required")
    private Long senderId;
    
    @NotNull(message = "Recipient ID is required")
    private Long recipientId;
    
    @NotNull(message = "Invitation type is required")
    private Invitation.InvitationType type;
    
    // Required for BOARD_COLLABORATION type, null for CONNECTION type
    private Long referenceId;
    
    private String message;
    
    // Required for BOARD_COLLABORATION type, null for CONNECTION type
    private BoardCollaboration.PermissionLevel permissionLevel;
}