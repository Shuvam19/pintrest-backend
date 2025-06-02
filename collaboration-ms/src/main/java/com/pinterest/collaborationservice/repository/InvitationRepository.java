package com.pinterest.collaborationservice.repository;

import com.pinterest.collaborationservice.model.Invitation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long> {

    // Find all invitations received by a user
    List<Invitation> findByRecipientId(Long recipientId);
    
    // Find all invitations sent by a user
    List<Invitation> findBySenderId(Long senderId);
    
    // Find all pending invitations for a user
    List<Invitation> findByRecipientIdAndStatus(Long recipientId, Invitation.InvitationStatus status);
    
    // Find all invitations of a specific type for a user
    List<Invitation> findByRecipientIdAndType(Long recipientId, Invitation.InvitationType type);
    
    // Find all pending invitations of a specific type for a user
    List<Invitation> findByRecipientIdAndTypeAndStatus(
            Long recipientId, 
            Invitation.InvitationType type, 
            Invitation.InvitationStatus status);
    
    // Find a specific invitation between two users
    Optional<Invitation> findBySenderIdAndRecipientIdAndTypeAndReferenceId(
            Long senderId, 
            Long recipientId, 
            Invitation.InvitationType type, 
            Long referenceId);
    
    // Find a specific invitation for a board collaboration
    Optional<Invitation> findBySenderIdAndRecipientIdAndTypeAndReferenceIdAndStatus(
            Long senderId, 
            Long recipientId, 
            Invitation.InvitationType type, 
            Long referenceId, 
            Invitation.InvitationStatus status);
    
    // Paginated version for received invitations
    Page<Invitation> findByRecipientId(Long recipientId, Pageable pageable);
    
    // Paginated version for sent invitations
    Page<Invitation> findBySenderId(Long senderId, Pageable pageable);
    
    // Paginated version for pending invitations
    Page<Invitation> findByRecipientIdAndStatus(
            Long recipientId, 
            Invitation.InvitationStatus status, 
            Pageable pageable);
    
    // Count pending invitations for a user
    long countByRecipientIdAndStatus(Long recipientId, Invitation.InvitationStatus status);
    
    // Find all invitations for a specific board
    List<Invitation> findByTypeAndReferenceId(Invitation.InvitationType type, Long referenceId);
    
    // Find all pending invitations for a specific board
    List<Invitation> findByTypeAndReferenceIdAndStatus(
            Invitation.InvitationType type, 
            Long referenceId, 
            Invitation.InvitationStatus status);
    
    // Find expired invitations (created more than X days ago and still pending)
    @Query("SELECT i FROM Invitation i WHERE i.status = 'PENDING' AND i.createdAt < :cutoffDate")
    List<Invitation> findExpiredInvitations(@Param("cutoffDate") LocalDateTime cutoffDate);
}