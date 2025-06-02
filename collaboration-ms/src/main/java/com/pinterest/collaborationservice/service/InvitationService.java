package com.pinterest.collaborationservice.service;

import com.pinterest.collaborationservice.dto.InvitationDto;
import com.pinterest.collaborationservice.dto.InvitationRequest;
import com.pinterest.collaborationservice.dto.InvitationResponseRequest;
import com.pinterest.collaborationservice.model.Invitation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface InvitationService {

    // Create a new invitation
    InvitationDto createInvitation(InvitationRequest invitationRequest);
    
    // Get a specific invitation by ID
    InvitationDto getInvitationById(Long invitationId);
    
    // Respond to an invitation (accept, decline, ignore)
    InvitationDto respondToInvitation(InvitationResponseRequest responseRequest);
    
    // Get all invitations received by a user
    List<InvitationDto> getReceivedInvitations(Long recipientId);
    
    // Get all invitations sent by a user
    List<InvitationDto> getSentInvitations(Long senderId);
    
    // Get all pending invitations for a user
    List<InvitationDto> getPendingInvitations(Long recipientId);
    
    // Get all invitations of a specific type for a user
    List<InvitationDto> getInvitationsByType(Long recipientId, Invitation.InvitationType type);
    
    // Get paginated list of received invitations
    Page<InvitationDto> getReceivedInvitations(Long recipientId, Pageable pageable);
    
    // Get paginated list of sent invitations
    Page<InvitationDto> getSentInvitations(Long senderId, Pageable pageable);
    
    // Get paginated list of pending invitations
    Page<InvitationDto> getPendingInvitations(Long recipientId, Pageable pageable);
    
    // Get count of pending invitations for a user
    long getPendingInvitationsCount(Long recipientId);
    
    // Cancel an invitation (by sender)
    void cancelInvitation(Long invitationId, Long senderId);
    
    // Delete an invitation (by admin)
    void deleteInvitation(Long invitationId);
    
    // Get all invitations for a specific board
    List<InvitationDto> getBoardInvitations(Long boardId);
    
    // Get all pending invitations for a specific board
    List<InvitationDto> getPendingBoardInvitations(Long boardId);
    
    // Process expired invitations
    void processExpiredInvitations(int expirationDays);
}