package com.pinterest.collaborationservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "invitations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // User who sent the invitation
    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    // User who received the invitation
    @Column(name = "recipient_id", nullable = false)
    private Long recipientId;

    // Type of invitation
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvitationType type;

    // Reference ID (board_id for BOARD_COLLABORATION, null for CONNECTION)
    @Column(name = "reference_id")
    private Long referenceId;

    // Status of the invitation
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvitationStatus status;

    // Message included with the invitation
    @Column(columnDefinition = "TEXT")
    private String message;

    // For board collaborations - the permission level being offered
    @Enumerated(EnumType.STRING)
    private BoardCollaboration.PermissionLevel permissionLevel;

    // When the invitation was responded to
    private LocalDateTime respondedAt;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Enum for invitation types
    public enum InvitationType {
        CONNECTION,           // Invitation to connect/follow
        BOARD_COLLABORATION   // Invitation to collaborate on a board
    }

    // Enum for invitation status
    public enum InvitationStatus {
        PENDING,
        ACCEPTED,
        DECLINED,
        IGNORED,
        EXPIRED
    }
}