package com.pinterest.collaborationservice.service;

import com.pinterest.collaborationservice.dto.BoardCollaborationDto;
import com.pinterest.collaborationservice.exception.ResourceNotFoundException;
import com.pinterest.collaborationservice.model.BoardCollaboration;
import com.pinterest.collaborationservice.repository.BoardCollaborationRepository;
import com.pinterest.collaborationservice.service.impl.BoardCollaborationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BoardCollaborationServiceImplTest {

    @Mock
    private BoardCollaborationRepository boardCollaborationRepository;

    @InjectMocks
    private BoardCollaborationServiceImpl boardCollaborationService;

    private BoardCollaboration boardCollaboration;
    private BoardCollaborationDto boardCollaborationDto;

    @BeforeEach
    void setUp() {
        boardCollaboration = BoardCollaboration.builder()
                .id(1L)
                .boardId(101L)
                .userId(201L)
                .invitedBy(301L)
                .permissionLevel(BoardCollaboration.PermissionLevel.EDIT)
                .status(BoardCollaboration.CollaborationStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

        boardCollaborationDto = BoardCollaborationDto.builder()
                .id(1L)
                .boardId(101L)
                .userId(201L)
                .invitedBy(301L)
                .permissionLevel(BoardCollaboration.PermissionLevel.EDIT)
                .status(BoardCollaboration.CollaborationStatus.ACTIVE)
                .createdAt(boardCollaboration.getCreatedAt())
                .build();
    }

    @Test
    @DisplayName("Should add collaborator successfully")
    void shouldAddCollaborator() {
        when(boardCollaborationRepository.save(any(BoardCollaboration.class)))
                .thenReturn(boardCollaboration);

        BoardCollaborationDto result = boardCollaborationService.addCollaborator(
                101L, 201L, 301L, BoardCollaboration.PermissionLevel.EDIT);

        assertThat(result).isNotNull();
        assertThat(result.getBoardId()).isEqualTo(101L);
        assertThat(result.getUserId()).isEqualTo(201L);
        assertThat(result.getPermissionLevel()).isEqualTo(BoardCollaboration.PermissionLevel.EDIT);
        verify(boardCollaborationRepository, times(1)).save(any(BoardCollaboration.class));
    }

    @Test
    @DisplayName("Should get collaboration by board ID and user ID")
    void shouldGetCollaboration() {
        when(boardCollaborationRepository.findByBoardIdAndUserId(101L, 201L))
                .thenReturn(Optional.of(boardCollaboration));

        BoardCollaborationDto result = boardCollaborationService.getCollaboration(101L, 201L);

        assertThat(result).isNotNull();
        assertThat(result.getBoardId()).isEqualTo(101L);
        assertThat(result.getUserId()).isEqualTo(201L);
        verify(boardCollaborationRepository, times(1)).findByBoardIdAndUserId(101L, 201L);
    }

    @Test
    @DisplayName("Should throw exception when collaboration not found")
    void shouldThrowExceptionWhenCollaborationNotFound() {
        when(boardCollaborationRepository.findByBoardIdAndUserId(999L, 999L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            boardCollaborationService.getCollaboration(999L, 999L);
        });

        verify(boardCollaborationRepository, times(1)).findByBoardIdAndUserId(999L, 999L);
    }

    @Test
    @DisplayName("Should update collaboration status")
    void shouldUpdateCollaborationStatus() {
        when(boardCollaborationRepository.findById(1L))
                .thenReturn(Optional.of(boardCollaboration));
        when(boardCollaborationRepository.save(any(BoardCollaboration.class)))
                .thenReturn(boardCollaboration);

        BoardCollaborationDto result = boardCollaborationService.updateCollaborationStatus(
                1L, BoardCollaboration.CollaborationStatus.INACTIVE);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(BoardCollaboration.CollaborationStatus.INACTIVE);
        verify(boardCollaborationRepository, times(1)).findById(1L);
        verify(boardCollaborationRepository, times(1)).save(any(BoardCollaboration.class));
    }

    @Test
    @DisplayName("Should update permission level")
    void shouldUpdatePermissionLevel() {
        when(boardCollaborationRepository.findById(1L))
                .thenReturn(Optional.of(boardCollaboration));
        when(boardCollaborationRepository.save(any(BoardCollaboration.class)))
                .thenReturn(boardCollaboration);

        BoardCollaborationDto result = boardCollaborationService.updatePermissionLevel(
                1L, BoardCollaboration.PermissionLevel.VIEW);

        assertThat(result).isNotNull();
        assertThat(result.getPermissionLevel()).isEqualTo(BoardCollaboration.PermissionLevel.VIEW);
        verify(boardCollaborationRepository, times(1)).findById(1L);
        verify(boardCollaborationRepository, times(1)).save(any(BoardCollaboration.class));
    }

    @Test
    @DisplayName("Should get board collaborators")
    void shouldGetBoardCollaborators() {
        when(boardCollaborationRepository.findByBoardId(101L))
                .thenReturn(List.of(boardCollaboration));

        List<BoardCollaborationDto> result = boardCollaborationService.getBoardCollaborators(101L);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBoardId()).isEqualTo(101L);
        verify(boardCollaborationRepository, times(1)).findByBoardId(101L);
    }

    @Test
    @DisplayName("Should get user collaborations")
    void shouldGetUserCollaborations() {
        when(boardCollaborationRepository.findByUserId(201L))
                .thenReturn(List.of(boardCollaboration));

        List<BoardCollaborationDto> result = boardCollaborationService.getUserCollaborations(201L);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo(201L);
        verify(boardCollaborationRepository, times(1)).findByUserId(201L);
    }

    @Test
    @DisplayName("Should get board collaborators with pagination")
    void shouldGetBoardCollaboratorsPaged() {
        Page<BoardCollaboration> page = new PageImpl<>(List.of(boardCollaboration));
        when(boardCollaborationRepository.findByBoardId(eq(101L), any(Pageable.class)))
                .thenReturn(page);

        Page<BoardCollaborationDto> result = boardCollaborationService.getBoardCollaborators(101L, Pageable.unpaged());

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getBoardId()).isEqualTo(101L);
        verify(boardCollaborationRepository, times(1)).findByBoardId(eq(101L), any(Pageable.class));
    }
}