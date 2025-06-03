package com.pinterest.contentservice.repository;

import com.pinterest.contentservice.model.Board;
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

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class BoardRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BoardRepository boardRepository;

    private Board board1;
    private Board board2;
    private Board board3;

    @BeforeEach
    void setUp() {
        board1 = Board.builder()
                .userId(101L)
                .name("Travel Ideas")
                .description("Places I want to visit")
                .isPrivate(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        board2 = Board.builder()
                .userId(101L)
                .name("Food Recipes")
                .description("Delicious recipes to try")
                .isPrivate(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        board3 = Board.builder()
                .userId(102L)
                .name("Home Decor")
                .description("Ideas for home decoration")
                .isPrivate(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        entityManager.persist(board1);
        entityManager.persist(board2);
        entityManager.persist(board3);
        entityManager.flush();
    }

    @Test
    @DisplayName("Should find boards by user ID")
    void shouldFindBoardsByUserId() {
        List<Board> boards = boardRepository.findByUserId(101L);

        assertThat(boards).isNotEmpty();
        assertThat(boards).hasSize(2);
        assertThat(boards.get(0).getUserId()).isEqualTo(101L);
        assertThat(boards.get(1).getUserId()).isEqualTo(101L);
    }

    @Test
    @DisplayName("Should find boards by user ID with pagination")
    void shouldFindBoardsByUserIdWithPagination() {
        Page<Board> boardsPage = boardRepository.findByUserId(101L, PageRequest.of(0, 10));

        assertThat(boardsPage).isNotEmpty();
        assertThat(boardsPage.getContent()).hasSize(2);
        assertThat(boardsPage.getContent().get(0).getUserId()).isEqualTo(101L);
        assertThat(boardsPage.getContent().get(1).getUserId()).isEqualTo(101L);
    }

    @Test
    @DisplayName("Should find public boards by user ID")
    void shouldFindPublicBoardsByUserId() {
        List<Board> boards = boardRepository.findByUserIdAndIsPrivate(101L, false);

        assertThat(boards).isNotEmpty();
        assertThat(boards).hasSize(1);
        assertThat(boards.get(0).getUserId()).isEqualTo(101L);
        assertThat(boards.get(0).isPrivate()).isFalse();
    }

    @Test
    @DisplayName("Should find private boards by user ID")
    void shouldFindPrivateBoardsByUserId() {
        List<Board> boards = boardRepository.findByUserIdAndIsPrivate(101L, true);

        assertThat(boards).isNotEmpty();
        assertThat(boards).hasSize(1);
        assertThat(boards.get(0).getUserId()).isEqualTo(101L);
        assertThat(boards.get(0).isPrivate()).isTrue();
    }

    @Test
    @DisplayName("Should find all public boards")
    void shouldFindAllPublicBoards() {
        List<Board> boards = boardRepository.findByIsPrivate(false);

        assertThat(boards).isNotEmpty();
        assertThat(boards).hasSize(2);
        assertThat(boards.get(0).isPrivate()).isFalse();
        assertThat(boards.get(1).isPrivate()).isFalse();
    }

    @Test
    @DisplayName("Should find boards by name containing keyword")
    void shouldFindBoardsByNameContaining() {
        List<Board> boards = boardRepository.findByNameContainingIgnoreCase("travel");

        assertThat(boards).isNotEmpty();
        assertThat(boards).hasSize(1);
        assertThat(boards.get(0).getName()).contains("Travel");
    }

    @Test
    @DisplayName("Should find boards by description containing keyword")
    void shouldFindBoardsByDescriptionContaining() {
        List<Board> boards = boardRepository.findByDescriptionContainingIgnoreCase("home");

        assertThat(boards).isNotEmpty();
        assertThat(boards).hasSize(1);
        assertThat(boards.get(0).getDescription()).contains("home");
    }

    @Test
    @DisplayName("Should find public boards with pagination")
    void shouldFindPublicBoardsWithPagination() {
        Page<Board> boardsPage = boardRepository.findByIsPrivate(false, PageRequest.of(0, 10));

        assertThat(boardsPage).isNotEmpty();
        assertThat(boardsPage.getContent()).hasSize(2);
        assertThat(boardsPage.getContent().get(0).isPrivate()).isFalse();
        assertThat(boardsPage.getContent().get(1).isPrivate()).isFalse();
    }
}