package com.pinterest.contentservice.repository;

import com.pinterest.contentservice.model.Comment;
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
public class CommentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CommentRepository commentRepository;

    private Comment comment1;
    private Comment comment2;
    private Comment comment3;

    @BeforeEach
    void setUp() {
        comment1 = Comment.builder()
                .pinId(101L)
                .userId(201L)
                .content("Great pin!")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        comment2 = Comment.builder()
                .pinId(101L)
                .userId(202L)
                .content("Love this idea!")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        comment3 = Comment.builder()
                .pinId(102L)
                .userId(201L)
                .content("Nice design!")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        entityManager.persist(comment1);
        entityManager.persist(comment2);
        entityManager.persist(comment3);
        entityManager.flush();
    }

    @Test
    @DisplayName("Should find comments by pin ID")
    void shouldFindCommentsByPinId() {
        List<Comment> comments = commentRepository.findByPinId(101L);

        assertThat(comments).isNotEmpty();
        assertThat(comments).hasSize(2);
        assertThat(comments.get(0).getPinId()).isEqualTo(101L);
        assertThat(comments.get(1).getPinId()).isEqualTo(101L);
    }

    @Test
    @DisplayName("Should find comments by pin ID with pagination")
    void shouldFindCommentsByPinIdWithPagination() {
        Page<Comment> commentsPage = commentRepository.findByPinId(
                101L, PageRequest.of(0, 10));

        assertThat(commentsPage).isNotEmpty();
        assertThat(commentsPage.getContent()).hasSize(2);
        assertThat(commentsPage.getContent().get(0).getPinId()).isEqualTo(101L);
        assertThat(commentsPage.getContent().get(1).getPinId()).isEqualTo(101L);
    }

    @Test
    @DisplayName("Should find comments by user ID")
    void shouldFindCommentsByUserId() {
        List<Comment> comments = commentRepository.findByUserId(201L);

        assertThat(comments).isNotEmpty();
        assertThat(comments).hasSize(2);
        assertThat(comments.get(0).getUserId()).isEqualTo(201L);
        assertThat(comments.get(1).getUserId()).isEqualTo(201L);
    }

    @Test
    @DisplayName("Should find comments by user ID with pagination")
    void shouldFindCommentsByUserIdWithPagination() {
        Page<Comment> commentsPage = commentRepository.findByUserId(
                201L, PageRequest.of(0, 10));

        assertThat(commentsPage).isNotEmpty();
        assertThat(commentsPage.getContent()).hasSize(2);
        assertThat(commentsPage.getContent().get(0).getUserId()).isEqualTo(201L);
        assertThat(commentsPage.getContent().get(1).getUserId()).isEqualTo(201L);
    }

    @Test
    @DisplayName("Should find comments by pin ID and user ID")
    void shouldFindCommentsByPinIdAndUserId() {
        List<Comment> comments = commentRepository.findByPinIdAndUserId(101L, 201L);

        assertThat(comments).isNotEmpty();
        assertThat(comments).hasSize(1);
        assertThat(comments.get(0).getPinId()).isEqualTo(101L);
        assertThat(comments.get(0).getUserId()).isEqualTo(201L);
    }

    @Test
    @DisplayName("Should find comments containing specific text")
    void shouldFindCommentsByContentContaining() {
        List<Comment> comments = commentRepository.findByContentContainingIgnoreCase("great");

        assertThat(comments).isNotEmpty();
        assertThat(comments).hasSize(1);
        assertThat(comments.get(0).getContent()).contains("Great");
    }

    @Test
    @DisplayName("Should count comments by pin ID")
    void shouldCountCommentsByPinId() {
        long count = commentRepository.countByPinId(101L);

        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("Should count comments by user ID")
    void shouldCountCommentsByUserId() {
        long count = commentRepository.countByUserId(201L);

        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("Should delete comments by pin ID")
    void shouldDeleteCommentsByPinId() {
        commentRepository.deleteByPinId(101L);
        entityManager.flush();
        entityManager.clear();

        List<Comment> remainingComments = commentRepository.findAll();
        assertThat(remainingComments).hasSize(1);
        assertThat(remainingComments.get(0).getPinId()).isEqualTo(102L);
    }
}