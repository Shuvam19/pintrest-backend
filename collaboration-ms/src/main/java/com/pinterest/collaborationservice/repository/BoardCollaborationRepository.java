package com.pinterest.collaborationservice.repository;

import com.pinterest.collaborationservice.model.BoardCollaboration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardCollaborationRepository extends JpaRepository<BoardCollaboration, Long> {

    // Find a specific collaboration between a user and a board
    Optional<BoardCollaboration> findByBoardIdAndUserId(Long boardId, Long userId);
    
    // Find all collaborations for a specific board
    List<BoardCollaboration> findByBoardId(Long boardId);
    
    // Find all collaborations for a specific board with a specific status
    List<BoardCollaboration> findByBoardIdAndStatus(Long boardId, BoardCollaboration.CollaborationStatus status);
    
    // Find all boards a user is collaborating on
    List<BoardCollaboration> findByUserId(Long userId);
    
    // Find all boards a user is collaborating on with a specific status
    List<BoardCollaboration> findByUserIdAndStatus(Long userId, BoardCollaboration.CollaborationStatus status);
    
    // Paginated version for board collaborators
    Page<BoardCollaboration> findByBoardId(Long boardId, Pageable pageable);
    
    // Paginated version for user's collaborations
    Page<BoardCollaboration> findByUserId(Long userId, Pageable pageable);
    
    // Count collaborators for a board
    long countByBoardIdAndStatus(Long boardId, BoardCollaboration.CollaborationStatus status);
    
    // Check if a user is a collaborator on a board
    boolean existsByBoardIdAndUserIdAndStatus(Long boardId, Long userId, BoardCollaboration.CollaborationStatus status);
    
    // Find collaborations by permission level
    List<BoardCollaboration> findByBoardIdAndPermissionLevel(Long boardId, BoardCollaboration.PermissionLevel permissionLevel);
    
    // Find boards where user has specific permission level
    List<BoardCollaboration> findByUserIdAndPermissionLevel(Long userId, BoardCollaboration.PermissionLevel permissionLevel);
    
    // Find collaborations by who invited the user
    List<BoardCollaboration> findByBoardIdAndInvitedBy(Long boardId, Long invitedBy);
    
    // Find all boards a user has admin access to
    @Query("SELECT bc FROM BoardCollaboration bc WHERE bc.userId = :userId AND bc.permissionLevel = 'ADMIN' AND bc.status = 'ACCEPTED'")
    List<BoardCollaboration> findAdminBoards(@Param("userId") Long userId);
}