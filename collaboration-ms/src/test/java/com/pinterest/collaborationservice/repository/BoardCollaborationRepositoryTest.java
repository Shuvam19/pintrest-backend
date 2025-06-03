package com.pinterest.collaborationservice.repository;

import com.pinterest.collaborationservice.model.BoardCollaboration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class BoardCollaborationRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BoardCollaborationRepository collaborationRepository;

    private BoardCollaboration collaboration1;
    private BoardCollaboration collaboration2;
    private BoardCollaboration collaboration3;

    @BeforeEach
    void setUp() {
        collaboration1 = BoardCollaboration.builder()
                .boardId(101L)
                .userId(201L)
                .permissionLevel(BoardCollaboration.PermissionLevel.EDIT)
                .status(BoardCollaboration.CollaborationStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        collaboration2 = BoardCollaboration.builder()
                .boardId(101L)
                .userId(202L)
                .permissionLevel(BoardCollaboration.PermissionLevel.VIEW)
                .status(BoardCollaboration.CollaborationStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        collaboration3 = BoardCollaboration.builder()
                .boardId(102L)
                .userId(201L)
                .permissionLevel(BoardCollaboration.PermissionLevel.EDIT)
                .status(BoardCollaboration.CollaborationStatus.INACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        entityManager.persist(collaboration1);
        entityManager.persist(collaboration2);
        entityManager.persist(collaboration3);
        entityManager.flush();
    }

    @Test
    @DisplayName("Should find collaboration by board ID and user ID")
    void shouldFindCollaborationByBoardIdAndUserId() {
        Optional<BoardCollaboration> result = collaborationRepository.findByBoardIdAndUserId(101L, 201L);

        assertThat(result).isPresent();
        assertThat(result.get().getBoardId()).isEqualTo(101L);
        assertThat(result.get().getUserId()).isEqualTo(201L);
    }

    @Test
    @DisplayName("Should find collaborations by board ID")
    void shouldFindCollaborationsByBoardId() {
        List<BoardCollaboration> collaborations = collaborationRepository.findByBoardId(101L);

        assertThat(collaborations).isNotEmpty();
        assertThat(collaborations).hasSize(2);
        assertThat(collaborations.get(0).getBoardId()).isEqualTo(101L);
        assertThat(collaborations.get(1).getBoardId()).isEqualTo(101L);
    }

    @Test
    @DisplayName("Should find collaborations by board ID with pagination")
    void shouldFindCollaborationsByBoardIdWithPagination() {
        Page<BoardCollaboration> collaborationsPage = collaborationRepository.findByBoardId(
                101L, PageRequest.of(0, 10));

        assertThat(collaborationsPage).isNotEmpty();
        assertThat(collaborationsPage.getContent()).hasSize(2);
        assertThat(collaborationsPage.getContent().get(0).getBoardId()).isEqualTo(101L);
        assertThat(collaborationsPage.getContent().get(1).getBoardId()).isEqualTo(101L);
    }

    @Test
    @DisplayName("Should find collaborations by user ID")
    void shouldFindCollaborationsByUserId() {
        List<BoardCollaboration> collaborations = collaborationRepository.findByUserId(201L);

        assertThat(collaborations).isNotEmpty();
        assertThat(collaborations).hasSize(2);
        assertThat(collaborations.get(0).getUserId()).isEqualTo(201L);
        assertThat(collaborations.get(1).getUserId()).isEqualTo(201L);
    }

    @Test
    @DisplayName("Should find collaborations by user ID with pagination")
    void shouldFindCollaborationsByUserIdWithPagination() {
        Page<BoardCollaboration> collaborationsPage = collaborationRepository.findByUserId(
                201L, PageRequest.of(0, 10));

        assertThat(collaborationsPage).isNotEmpty();
        assertThat(collaborationsPage.getContent()).hasSize(2);
        assertThat(collaborationsPage.getContent().get(0).getUserId()).isEqualTo(201L);
        assertThat(collaborationsPage.getContent().get(1).getUserId()).isEqualTo(201L);
    }

    @Test
    @DisplayName("Should find active collaborations by board ID")
    void shouldFindActiveCollaborationsByBoardId() {
        List<BoardCollaboration> collaborations = collaborationRepository.findByBoardIdAndStatus(
                101L, BoardCollaboration.CollaborationStatus.ACTIVE);

        assertThat(collaborations).isNotEmpty();
        assertThat(collaborations).hasSize(2);
        assertThat(collaborations.get(0).getBoardId()).isEqualTo(101L);
        assertThat(collaborations.get(0).getStatus()).isEqualTo(BoardCollaboration.CollaborationStatus.ACTIVE);
        assertThat(collaborations.get(1).getBoardId()).isEqualTo(101L);
        assertThat(collaborations.get(1).getStatus()).isEqualTo(BoardCollaboration.CollaborationStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should find active collaborations by user ID")
    void shouldFindActiveCollaborationsByUserId() {
        List<BoardCollaboration> collaborations = collaborationRepository.findByUserIdAndStatus(
                201L, BoardCollaboration.CollaborationStatus.ACTIVE);

        assertThat(collaborations).isNotEmpty();
        assertThat(collaborations).hasSize(1);
        assertThat(collaborations.get(0).getUserId()).isEqualTo(201L);
        assertThat(collaborations.get(0).getStatus()).isEqualTo(BoardCollaboration.CollaborationStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should find collaborations by permission level")
    void shouldFindCollaborationsByPermissionLevel() {
        List<BoardCollaboration> collaborations = collaborationRepository.findByPermissionLevel(
                BoardCollaboration.PermissionLevel.EDIT);

        assertThat(collaborations).isNotEmpty();
        assertThat(collaborations).hasSize(2);
        assertThat(collaborations.get(0).getPermissionLevel()).isEqualTo(BoardCollaboration.PermissionLevel.EDIT);
        assertThat(collaborations.get(1).getPermissionLevel()).isEqualTo(BoardCollaboration.PermissionLevel.EDIT);
    }

    @Test
    @DisplayName("Should count collaborations by board ID")
    void shouldCountCollaborationsByBoardId() {
        long count = collaborationRepository.countByBoardId(101L);

        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("Should count active collaborations by board ID")
    void shouldCountActiveCollaborationsByBoardId() {
        long count = collaborationRepository.countByBoardIdAndStatus(
                101L, BoardCollaboration.CollaborationStatus.ACTIVE);

        assertThat(count).isEqualTo(2);
    }
}