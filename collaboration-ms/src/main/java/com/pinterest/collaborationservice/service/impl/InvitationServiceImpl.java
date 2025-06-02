package com.pinterest.collaborationservice.service.impl;

import com.pinterest.collaborationservice.dto.InvitationDto;
import com.pinterest.collaborationservice.dto.InvitationRequest;
import com.pinterest.collaborationservice.dto.InvitationResponseRequest;
import com.pinterest.collaborationservice.exception.ResourceNotFoundException;
import com.pinterest.collaborationservice.model.BoardCollaboration;
import com.pinterest.collaborationservice.model.Invitation;
import com.pinterest.collaborationservice.model.UserConnection;
import com.pinterest.collaborationservice.repository.InvitationRepository;
import com.pinterest.collaborationservice.service.BoardCollaborationService;
import com.pinterest.collaborationservice.service.InvitationService;
import com.pinterest.collaborationservice.service.UserConnectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvitationServiceImpl implements InvitationService {

    private final InvitationRepository invitationRepository;
    private final UserConnectionService userConnectionService;
    private final BoardCollaborationService boardCollaborationService;

    @Override
    @Transactional
    public InvitationDto createInvitation(InvitationRequest request) {
        // Validate invitation type-specific requirements
        validateInvitationRequest(request);
        
        // Check if there's already a pending invitation
        if (invitationRepository.existsBySenderIdAndRecipientIdAndTypeAndReferenceIdAndStatus(
                request.getSenderId(), 
                request.getRecipientId(), 
                Invitation.InvitationType.valueOf(request.getType()), 
                request.getReferenceId(), 
                Invitation.InvitationStatus.PENDING)) {
            throw new IllegalStateException("A pending invitation already exists");
        }
        
        Invitation invitation = Invitation.builder()
                .senderId(request.getSenderId())
                .recipientId(request.getRecipientId())
                .type(Invitation.InvitationType.valueOf(request.getType()))
                .referenceId(request.getReferenceId())
                .status(Invitation.InvitationStatus.PENDING)
                .message(request.getMessage())
                .permissionLevel(request.getPermissionLevel() != null ? 
                        BoardCollaboration.PermissionLevel.valueOf(request.getPermissionLevel()) : null)
                .build();
        
        Invitation savedInvitation = invitationRepository.save(invitation);
        return mapToDto(savedInvitation);
    }

    @Override
    public InvitationDto getInvitation(Long invitationId) {
        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new ResourceNotFoundException("Invitation not found with id: " + invitationId));
        return mapToDto(invitation);
    }

    @Override
    @Transactional
    public InvitationDto respondToInvitation(InvitationResponseRequest request) {
        Invitation invitation = invitationRepository.findById(request.getInvitationId())
                .orElseThrow(() -> new ResourceNotFoundException("Invitation not found with id: " + request.getInvitationId()));
        
        if (invitation.getStatus() != Invitation.InvitationStatus.PENDING) {
            throw new IllegalStateException("Invitation has already been processed");
        }
        
        Invitation.InvitationStatus newStatus = Invitation.InvitationStatus.valueOf(request.getStatus());
        invitation.setStatus(newStatus);
        invitation.setRespondedAt(LocalDateTime.now());
        
        // Process the invitation based on type and response
        if (newStatus == Invitation.InvitationStatus.ACCEPTED) {
            processAcceptedInvitation(invitation);
        }
        
        Invitation updatedInvitation = invitationRepository.save(invitation);
        return mapToDto(updatedInvitation);
    }

    @Override
    public Page<InvitationDto> getSentInvitations(Long senderId, Pageable pageable) {
        Page<Invitation> invitations = invitationRepository.findBySenderId(senderId, pageable);
        return invitations.map(this::mapToDto);
    }

    @Override
    public Page<InvitationDto> getReceivedInvitations(Long recipientId, Pageable pageable) {
        Page<Invitation> invitations = invitationRepository.findByRecipientId(recipientId, pageable);
        return invitations.map(this::mapToDto);
    }

    @Override
    public Page<InvitationDto> getPendingInvitations(Long recipientId, Pageable pageable) {
        Page<Invitation> invitations = invitationRepository.findByRecipientIdAndStatus(
                recipientId, Invitation.InvitationStatus.PENDING, pageable);
        return invitations.map(this::mapToDto);
    }

    @Override
    public Page<InvitationDto> getInvitationsByType(Long recipientId, String type, Pageable pageable) {
        Page<Invitation> invitations = invitationRepository.findByRecipientIdAndType(
                recipientId, Invitation.InvitationType.valueOf(type), pageable);
        return invitations.map(this::mapToDto);
    }

    @Override
    public List<InvitationDto> getPendingInvitations(Long recipientId) {
        List<Invitation> invitations = invitationRepository.findByRecipientIdAndStatus(
                recipientId, Invitation.InvitationStatus.PENDING);
        return invitations.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public long countPendingInvitations(Long recipientId) {
        return invitationRepository.countByRecipientIdAndStatus(
                recipientId, Invitation.InvitationStatus.PENDING);
    }

    @Override
    @Transactional
    public InvitationDto cancelInvitation(Long invitationId, Long senderId) {
        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new ResourceNotFoundException("Invitation not found with id: " + invitationId));
        
        if (!invitation.getSenderId().equals(senderId)) {
            throw new IllegalStateException("Only the sender can cancel an invitation");
        }
        
        if (invitation.getStatus() != Invitation.InvitationStatus.PENDING) {
            throw new IllegalStateException("Only pending invitations can be canceled");
        }
        
        invitation.setStatus(Invitation.InvitationStatus.CANCELED);
        invitation.setRespondedAt(LocalDateTime.now());
        
        Invitation updatedInvitation = invitationRepository.save(invitation);
        return mapToDto(updatedInvitation);
    }

    @Override
    @Transactional
    public void deleteInvitation(Long invitationId) {
        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new ResourceNotFoundException("Invitation not found with id: " + invitationId));
        
        invitationRepository.delete(invitation);
    }

    @Override
    @Scheduled(cron = "0 0 * * * *") // Run every hour
    @Transactional
    public void processExpiredInvitations() {
        LocalDateTime expiryTime = LocalDateTime.now().minusDays(7); // Invitations expire after 7 days
        List<Invitation> expiredInvitations = invitationRepository.findExpiredPendingInvitations(expiryTime);
        
        for (Invitation invitation : expiredInvitations) {
            invitation.setStatus(Invitation.InvitationStatus.EXPIRED);
            invitationRepository.save(invitation);
        }
    }

    // Helper methods
    private void validateInvitationRequest(InvitationRequest request) {
        Invitation.InvitationType type = Invitation.InvitationType.valueOf(request.getType());
        
        switch (type) {
            case CONNECTION:
                // For connection invitations, referenceId should be null
                if (request.getReferenceId() != null) {
                    throw new IllegalArgumentException("Connection invitations should not have a referenceId");
                }
                break;
            case BOARD_COLLABORATION:
                // For board collaboration invitations, referenceId (boardId) and permissionLevel are required
                if (request.getReferenceId() == null) {
                    throw new IllegalArgumentException("Board collaboration invitations require a referenceId (boardId)");
                }
                if (request.getPermissionLevel() == null) {
                    throw new IllegalArgumentException("Board collaboration invitations require a permissionLevel");
                }
                break;
            default:
                throw new IllegalArgumentException("Unsupported invitation type: " + request.getType());
        }
    }

    private void processAcceptedInvitation(Invitation invitation) {
        switch (invitation.getType()) {
            case CONNECTION:
                // Create a user connection
                userConnectionService.createConnection(
                        invitation.getSenderId(),
                        invitation.getRecipientId(),
                        invitation.getMessage(),
                        true // Enable notifications by default
                );
                break;
            case BOARD_COLLABORATION:
                // Add user as a board collaborator
                boardCollaborationService.addCollaborator(
                        invitation.getReferenceId(), // boardId
                        invitation.getRecipientId(), // userId
                        invitation.getSenderId(), // invitedBy
                        invitation.getPermissionLevel() // permissionLevel
                );
                break;
        }
    }

    private InvitationDto mapToDto(Invitation invitation) {
        return InvitationDto.builder()
                .id(invitation.getId())
                .senderId(invitation.getSenderId())
                .recipientId(invitation.getRecipientId())
                .type(invitation.getType().name())
                .referenceId(invitation.getReferenceId())
                .status(invitation.getStatus().name())
                .message(invitation.getMessage())
                .permissionLevel(invitation.getPermissionLevel() != null ? invitation.getPermissionLevel().name() : null)
                .respondedAt(invitation.getRespondedAt())
                .createdAt(invitation.getCreatedAt())
                .updatedAt(invitation.getUpdatedAt())
                // Additional fields would be populated from User and Board services in a real implementation
                .build();
    }
}