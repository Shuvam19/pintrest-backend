package com.pinterest.contentservice.service;

import com.pinterest.contentservice.dto.BoardDto;
import com.pinterest.contentservice.dto.BoardRequest;
import com.pinterest.contentservice.exception.ResourceNotFoundException;
import com.pinterest.contentservice.model.Board;
import com.pinterest.contentservice.repository.BoardRepository;
import com.pinterest.contentservice.service.impl.BoardServiceImpl;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BoardServiceImplTest {

    @Mock
    private BoardRepository boardRepository;

    @InjectMocks
    private BoardServiceImpl boardService;

    private Board board;
    private BoardRequest boardRequest;

    @BeforeEach
    void setUp() {
        board = Board.builder()
                .id(1L)
                .userId(101L)
                .name("Travel Ideas")
                .description("Places I want to visit")
                .isPrivate(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        boardRequest = BoardRequest.builder()
                .userId(101L)
                .name("Travel Ideas")
                .description("Places I want to visit")
                .isPrivate(false)
                .build();
    }

    @Test
    @DisplayName("Should create board successfully")
    void shouldCreateBoard() {
        when(boardRepository.save(any(Board.class))).thenReturn(board);

        BoardDto result = boardService.createBoard(boardRequest);

        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(101L);
        assertThat(result.getName()).isEqualTo("Travel Ideas");
        assertThat(result.getDescription()).isEqualTo("Places I want to visit");
        assertThat(result.isPrivate()).isFalse();
        verify(boardRepository, times(1)).save(any(Board.class));
    }

    @Test
    @DisplayName("Should get board by ID")
    void shouldGetBoardById() {
        when(boardRepository.findById(1L)).thenReturn(Optional.of(board));

        BoardDto result = boardService.getBoardById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(boardRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when board not found")
    void shouldThrowExceptionWhenBoardNotFound() {
        when(boardRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            boardService.getBoardById(999L);
        });

        verify(boardRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should update board")
    void shouldUpdateBoard() {
        when(boardRepository.findById(1L)).thenReturn(Optional.of(board));
        when(boardRepository.save(any(Board.class))).thenReturn(board);

        BoardRequest updateRequest = BoardRequest.builder()
                .userId(101L)
                .name("Updated Travel Ideas")
                .description("Updated places I want to visit")
                .isPrivate(true)
                .build();

        BoardDto result = boardService.updateBoard(1L, updateRequest);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Updated Travel Ideas");
        assertThat(result.getDescription()).isEqualTo("Updated places I want to visit");
        assertThat(result.isPrivate()).isTrue();
        verify(boardRepository, times(1)).findById(1L);
        verify(boardRepository, times(1)).save(any(Board.class));
    }

    @Test
    @DisplayName("Should delete board")
    void shouldDeleteBoard() {
        when(boardRepository.findById(1L)).thenReturn(Optional.of(board));
        doNothing().when(boardRepository).delete(board);

        boardService.deleteBoard(1L);

        verify(boardRepository, times(1)).findById(1L);
        verify(boardRepository, times(1)).delete(board);
    }

    @Test
    @DisplayName("Should get boards by user ID")
    void shouldGetBoardsByUserId() {
        when(boardRepository.findByUserId(101L)).thenReturn(List.of(board));

        List<BoardDto> results = boardService.getBoardsByUserId(101L);

        assertThat(results).isNotEmpty();
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getUserId()).isEqualTo(101L);
        verify(boardRepository, times(1)).findByUserId(101L);
    }

    @Test
    @DisplayName("Should get boards by user ID with pagination")
    void shouldGetBoardsByUserIdWithPagination() {
        Page<Board> page = new PageImpl<>(List.of(board));
        when(boardRepository.findByUserId(eq(101L), any(Pageable.class)))
                .thenReturn(page);

        Page<BoardDto> results = boardService.getBoardsByUserId(101L, Pageable.unpaged());

        assertThat(results).isNotEmpty();
        assertThat(results.getContent()).hasSize(1);
        assertThat(results.getContent().get(0).getUserId()).isEqualTo(101L);
        verify(boardRepository, times(1))
                .findByUserId(eq(101L), any(Pageable.class));
    }

    @Test
    @DisplayName("Should get public boards by user ID")
    void shouldGetPublicBoardsByUserId() {
        when(boardRepository.findByUserIdAndIsPrivate(101L, false))
                .thenReturn(List.of(board));

        List<BoardDto> results = boardService.getPublicBoardsByUserId(101L);

        assertThat(results).isNotEmpty();
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getUserId()).isEqualTo(101L);
        assertThat(results.get(0).isPrivate()).isFalse();
        verify(boardRepository, times(1))
                .findByUserIdAndIsPrivate(101L, false);
    }

    @Test
    @DisplayName("Should get public boards")
    void shouldGetPublicBoards() {
        Page<Board> page = new PageImpl<>(List.of(board));
        when(boardRepository.findByIsPrivate(eq(false), any(Pageable.class)))
                .thenReturn(page);

        Page<BoardDto> results = boardService.getPublicBoards(Pageable.unpaged());

        assertThat(results).isNotEmpty();
        assertThat(results.getContent()).hasSize(1);
        assertThat(results.getContent().get(0).isPrivate()).isFalse();
        verify(boardRepository, times(1))
                .findByIsPrivate(eq(false), any(Pageable.class));
    }

    @Test
    @DisplayName("Should search boards by name")
    void shouldSearchBoardsByName() {
        when(boardRepository.findByNameContainingIgnoreCase("travel"))
                .thenReturn(List.of(board));

        List<BoardDto> results = boardService.searchBoardsByName("travel");

        assertThat(results).isNotEmpty();
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).contains("Travel");
        verify(boardRepository, times(1))
                .findByNameContainingIgnoreCase("travel");
    }
}