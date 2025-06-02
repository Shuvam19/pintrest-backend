package com.pinterest.collaborationservice.dto;

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
public class InvitationResponseRequest {
    
    @NotNull(message = "Invitation ID is required")
    private Long invitationId;
    
    @NotNull(message = "Response status is required")
    private Invitation.InvitationStatus status;
    
    // Optional response message
    private String responseMessage;
}