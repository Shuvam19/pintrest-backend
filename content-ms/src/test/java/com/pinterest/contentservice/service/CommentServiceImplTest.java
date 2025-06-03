package com.pinterest.contentservice.service;

import com.pinterest.contentservice.dto.CommentDto;
import com.pinterest.contentservice.dto.CommentRequest;
import com.pinterest.contentservice.exception.ResourceNotFoundException;
import com.pinterest.contentservice.model.Comment;
import com.pinterest.contentservice.repository.CommentRepository;
import com.pinterest.contentservice.service.impl.CommentServiceImpl;
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
public class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    private Comment comment;
    private CommentRequest commentRequest;

    @BeforeEach
    void setUp() {
        comment = Comment.builder()
                .id(1L)
                .pinId(101L)
                .userId(201L)
                .content("Great pin!")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        commentRequest = CommentRequest.builder()
                .pinId(101L)
                .userId(201L)
                .content("Great pin!")
                .build();
    }

    @Test
    @DisplayName("Should create comment successfully")
    void shouldCreateComment() {
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto result = commentService.createComment(commentRequest);

        assertThat(result).isNotNull();
        assertThat(result.getPinId()).isEqualTo(101L);
        assertThat(result.getUserId()).isEqualTo(201L);
        assertThat(result.getContent()).isEqualTo("Great pin!");
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    @DisplayName("Should get comment by ID")
    void shouldGetCommentById() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        CommentDto result = commentService.getCommentById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(commentRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when comment not found")
    void shouldThrowExceptionWhenCommentNotFound() {
        when(commentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            commentService.getCommentById(999L);
        });

        verify(commentRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should update comment")
    void shouldUpdateComment() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentRequest updateRequest = CommentRequest.builder()
                .pinId(101L)
                .userId(201L)
                .content("Updated: Really great pin!")
                .build();

        CommentDto result = commentService.updateComment(1L, updateRequest);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEqualTo("Updated: Really great pin!");
        verify(commentRepository, times(1)).findById(1L);
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    @DisplayName("Should delete comment")
    void shouldDeleteComment() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        doNothing().when(commentRepository).delete(comment);

        commentService.deleteComment(1L);

        verify(commentRepository, times(1)).findById(1L);
        verify(commentRepository, times(1)).delete(comment);
    }

    @Test
    @DisplayName("Should get comments by pin ID")
    void shouldGetCommentsByPinId() {
        when(commentRepository.findByPinId(101L)).thenReturn(List.of(comment));

        List<CommentDto> results = commentService.getCommentsByPinId(101L);

        assertThat(results).isNotEmpty();
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getPinId()).isEqualTo(101L);
        verify(commentRepository, times(1)).findByPinId(101L);
    }

    @Test
    @DisplayName("Should get comments by pin ID with pagination")
    void shouldGetCommentsByPinIdWithPagination() {
        Page<Comment> page = new PageImpl<>(List.of(comment));
        when(commentRepository.findByPinId(eq(101L), any(Pageable.class)))
                .thenReturn(page);

        Page<CommentDto> results = commentService.getCommentsByPinId(101L, Pageable.unpaged());

        assertThat(results).isNotEmpty();
        assertThat(results.getContent()).hasSize(1);
        assertThat(results.getContent().get(0).getPinId()).isEqualTo(101L);
        verify(commentRepository, times(1))
                .findByPinId(eq(101L), any(Pageable.class));
    }

    @Test
    @DisplayName("Should get comments by user ID")
    void shouldGetCommentsByUserId() {
        when(commentRepository.findByUserId(201L)).thenReturn(List.of(comment));

        List<CommentDto> results = commentService.getCommentsByUserId(201L);

        assertThat(results).isNotEmpty();
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getUserId()).isEqualTo(201L);
        verify(commentRepository, times(1)).findByUserId(201L);
    }

    @Test
    @DisplayName("Should get comments by user ID with pagination")
    void shouldGetCommentsByUserIdWithPagination() {
        Page<Comment> page = new PageImpl<>(List.of(comment));
        when(commentRepository.findByUserId(eq(201L), any(Pageable.class)))
                .thenReturn(page);

        Page<CommentDto> results = commentService.getCommentsByUserId(201L, Pageable.unpaged());

        assertThat(results).isNotEmpty();
        assertThat(results.getContent()).hasSize(1);
        assertThat(results.getContent().get(0).getUserId()).isEqualTo(201L);
        verify(commentRepository, times(1))
                .findByUserId(eq(201L), any(Pageable.class));
    }
}