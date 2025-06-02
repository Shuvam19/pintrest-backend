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
@Table(name = "board_collaborations", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"board_id", "user_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardCollaboration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "board_id", nullable = false)
    private Long boardId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    // User who invited this collaborator
    @Column(name = "invited_by")
    private Long invitedBy;

    // Status of the collaboration invitation
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CollaborationStatus status;

    // Permission level for this collaborator
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PermissionLevel permissionLevel;

    // Optional message with the invitation
    @Column(columnDefinition = "TEXT")
    private String invitationMessage;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Enum for collaboration status
    public enum CollaborationStatus {
        PENDING,
        ACCEPTED,
        DECLINED,
        REMOVED
    }

    // Enum for permission levels
    public enum PermissionLevel {
        VIEW,       // Can only view the board
        CONTRIBUTE, // Can add pins but not edit board settings
        EDIT,       // Can edit board settings and add pins
        ADMIN       // Full control including adding/removing collaborators
    }
}