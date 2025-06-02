package com.pinterest.collaborationservice.service;

import com.pinterest.collaborationservice.dto.BoardCollaborationDto;
import com.pinterest.collaborationservice.model.BoardCollaboration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BoardCollaborationService {

    // Add a collaborator to a board (directly, without invitation)
    BoardCollaborationDto addCollaborator(Long boardId, Long userId, Long invitedBy, BoardCollaboration.PermissionLevel permissionLevel);
    
    // Get a specific collaboration
    BoardCollaborationDto getCollaboration(Long boardId, Long userId);
    
    // Update collaboration status
    BoardCollaborationDto updateCollaborationStatus(Long collaborationId, BoardCollaboration.CollaborationStatus status);
    
    // Update collaboration permission level
    BoardCollaborationDto updatePermissionLevel(Long collaborationId, BoardCollaboration.PermissionLevel permissionLevel);
    
    // Remove a collaborator from a board
    void removeCollaborator(Long collaborationId);
    
    // Get all collaborators for a board
    List<BoardCollaborationDto> getBoardCollaborators(Long boardId);
    
    // Get all boards a user is collaborating on
    List<BoardCollaborationDto> getUserCollaborations(Long userId);
    
    // Get paginated list of collaborators for a board
    Page<BoardCollaborationDto> getBoardCollaborators(Long boardId, Pageable pageable);
    
    // Get paginated list of boards a user is collaborating on
    Page<BoardCollaborationDto> getUserCollaborations(Long userId, Pageable pageable);
    
    // Get count of collaborators for a board
    long getCollaboratorsCount(Long boardId);
    
    // Check if a user is a collaborator on a board
    boolean isCollaborator(Long boardId, Long userId);
    
    // Check if a user has a specific permission level on a board
    boolean hasPermission(Long boardId, Long userId, BoardCollaboration.PermissionLevel permissionLevel);
    
    // Get all boards where user has admin access
    List<BoardCollaborationDto> getAdminBoards(Long userId);
}