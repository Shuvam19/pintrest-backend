package com.pinterest.collaborationservice.service.impl;

import com.pinterest.collaborationservice.dto.BoardCollaborationDto;
import com.pinterest.collaborationservice.exception.ResourceNotFoundException;
import com.pinterest.collaborationservice.model.BoardCollaboration;
import com.pinterest.collaborationservice.repository.BoardCollaborationRepository;
import com.pinterest.collaborationservice.service.BoardCollaborationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardCollaborationServiceImpl implements BoardCollaborationService {

    private final BoardCollaborationRepository boardCollaborationRepository;

    @Override
    @Transactional
    public BoardCollaborationDto addCollaborator(Long boardId, Long userId, Long invitedBy, BoardCollaboration.PermissionLevel permissionLevel) {
        // Check if collaboration already exists
        if (boardCollaborationRepository.existsByBoardIdAndUserIdAndStatus(
                boardId, userId, BoardCollaboration.CollaborationStatus.ACCEPTED)) {
            throw new IllegalStateException("User is already a collaborator on this board");
        }
        
        // Create new collaboration
        BoardCollaboration collaboration = BoardCollaboration.builder()
                .boardId(boardId)
                .userId(userId)
                .invitedBy(invitedBy)
                .status(BoardCollaboration.CollaborationStatus.ACCEPTED) // Direct additions are accepted by default
                .permissionLevel(permissionLevel)
                .build();
        
        BoardCollaboration savedCollaboration = boardCollaborationRepository.save(collaboration);
        return mapToDto(savedCollaboration);
    }

    @Override
    public BoardCollaborationDto getCollaboration(Long boardId, Long userId) {
        BoardCollaboration collaboration = boardCollaborationRepository.findByBoardIdAndUserId(boardId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Collaboration not found"));
        return mapToDto(collaboration);
    }

    @Override
    @Transactional
    public BoardCollaborationDto updateCollaborationStatus(Long collaborationId, BoardCollaboration.CollaborationStatus status) {
        BoardCollaboration collaboration = boardCollaborationRepository.findById(collaborationId)
                .orElseThrow(() -> new ResourceNotFoundException("Collaboration not found with id: " + collaborationId));
        
        collaboration.setStatus(status);
        BoardCollaboration updatedCollaboration = boardCollaborationRepository.save(collaboration);
        return mapToDto(updatedCollaboration);
    }

    @Override
    @Transactional
    public BoardCollaborationDto updatePermissionLevel(Long collaborationId, BoardCollaboration.PermissionLevel permissionLevel) {
        BoardCollaboration collaboration = boardCollaborationRepository.findById(collaborationId)
                .orElseThrow(() -> new ResourceNotFoundException("Collaboration not found with id: " + collaborationId));
        
        collaboration.setPermissionLevel(permissionLevel);
        BoardCollaboration updatedCollaboration = boardCollaborationRepository.save(collaboration);
        return mapToDto(updatedCollaboration);
    }

    @Override
    @Transactional
    public void removeCollaborator(Long collaborationId) {
        BoardCollaboration collaboration = boardCollaborationRepository.findById(collaborationId)
                .orElseThrow(() -> new ResourceNotFoundException("Collaboration not found with id: " + collaborationId));
        
        boardCollaborationRepository.delete(collaboration);
    }

    @Override
    public List<BoardCollaborationDto> getBoardCollaborators(Long boardId) {
        List<BoardCollaboration> collaborations = boardCollaborationRepository.findByBoardIdAndStatus(
                boardId, BoardCollaboration.CollaborationStatus.ACCEPTED);
        return collaborations.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public List<BoardCollaborationDto> getUserCollaborations(Long userId) {
        List<BoardCollaboration> collaborations = boardCollaborationRepository.findByUserIdAndStatus(
                userId, BoardCollaboration.CollaborationStatus.ACCEPTED);
        return collaborations.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public Page<BoardCollaborationDto> getBoardCollaborators(Long boardId, Pageable pageable) {
        Page<BoardCollaboration> collaborations = boardCollaborationRepository.findByBoardId(boardId, pageable);
        return collaborations.map(this::mapToDto);
    }

    @Override
    public Page<BoardCollaborationDto> getUserCollaborations(Long userId, Pageable pageable) {
        Page<BoardCollaboration> collaborations = boardCollaborationRepository.findByUserId(userId, pageable);
        return collaborations.map(this::mapToDto);
    }

    @Override
    public long getCollaboratorsCount(Long boardId) {
        return boardCollaborationRepository.countByBoardIdAndStatus(
                boardId, BoardCollaboration.CollaborationStatus.ACCEPTED);
    }

    @Override
    public boolean isCollaborator(Long boardId, Long userId) {
        return boardCollaborationRepository.existsByBoardIdAndUserIdAndStatus(
                boardId, userId, BoardCollaboration.CollaborationStatus.ACCEPTED);
    }

    @Override
    public boolean hasPermission(Long boardId, Long userId, BoardCollaboration.PermissionLevel permissionLevel) {
        BoardCollaboration collaboration = boardCollaborationRepository.findByBoardIdAndUserId(boardId, userId)
                .orElse(null);
        
        if (collaboration == null || collaboration.getStatus() != BoardCollaboration.CollaborationStatus.ACCEPTED) {
            return false;
        }
        
        // Check if user has the required permission level or higher
        switch (permissionLevel) {
            case VIEW:
                return true; // All collaborators have at least VIEW permission
            case CONTRIBUTE:
                return collaboration.getPermissionLevel() == BoardCollaboration.PermissionLevel.CONTRIBUTE ||
                       collaboration.getPermissionLevel() == BoardCollaboration.PermissionLevel.EDIT ||
                       collaboration.getPermissionLevel() == BoardCollaboration.PermissionLevel.ADMIN;
            case EDIT:
                return collaboration.getPermissionLevel() == BoardCollaboration.PermissionLevel.EDIT ||
                       collaboration.getPermissionLevel() == BoardCollaboration.PermissionLevel.ADMIN;
            case ADMIN:
                return collaboration.getPermissionLevel() == BoardCollaboration.PermissionLevel.ADMIN;
            default:
                return false;
        }
    }

    @Override
    public List<BoardCollaborationDto> getAdminBoards(Long userId) {
        List<BoardCollaboration> collaborations = boardCollaborationRepository.findAdminBoards(userId);
        return collaborations.stream().map(this::mapToDto).collect(Collectors.toList());
    }
    
    // Helper method to map entity to DTO
    private BoardCollaborationDto mapToDto(BoardCollaboration collaboration) {
        return BoardCollaborationDto.builder()
                .id(collaboration.getId())
                .boardId(collaboration.getBoardId())
                .userId(collaboration.getUserId())
                .invitedBy(collaboration.getInvitedBy())
                .status(collaboration.getStatus())
                .permissionLevel(collaboration.getPermissionLevel())
                .invitationMessage(collaboration.getInvitationMessage())
                .createdAt(collaboration.getCreatedAt())
                .updatedAt(collaboration.getUpdatedAt())
                // Additional fields would be populated from Board and User services in a real implementation
                .build();
    }
}