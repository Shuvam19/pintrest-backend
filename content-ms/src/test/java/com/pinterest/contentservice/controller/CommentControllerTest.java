package com.pinterest.contentservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pinterest.contentservice.dto.CommentDto;
import com.pinterest.contentservice.dto.CommentRequest;
import com.pinterest.contentservice.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @Autowired
    private ObjectMapper objectMapper;

    private CommentDto commentDto;
    private CommentRequest commentRequest;

    @BeforeEach
    void setUp() {
        commentDto = CommentDto.builder()
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
    void shouldCreateComment() throws Exception {
        when(commentService.createComment(any(CommentRequest.class)))
                .thenReturn(commentDto);

        mockMvc.perform(post("/api/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.pinId").value(101L))
                .andExpect(jsonPath("$.data.userId").value(201L))
                .andExpect(jsonPath("$.data.content").value("Great pin!"));
    }

    @Test
    @DisplayName("Should get comment by ID")
    void shouldGetCommentById() throws Exception {
        when(commentService.getCommentById(1L))
                .thenReturn(commentDto);

        mockMvc.perform(get("/api/comments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1L));
    }

    @Test
    @DisplayName("Should update comment")
    void shouldUpdateComment() throws Exception {
        CommentRequest updateRequest = CommentRequest.builder()
                .pinId(101L)
                .userId(201L)
                .content("Updated: Really great pin!")
                .build();

        CommentDto updatedDto = CommentDto.builder()
                .id(1L)
                .pinId(101L)
                .userId(201L)
                .content("Updated: Really great pin!")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(commentService.updateComment(eq(1L), any(CommentRequest.class)))
                .thenReturn(updatedDto);

        mockMvc.perform(put("/api/comments/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").value("Updated: Really great pin!"));
    }

    @Test
    @DisplayName("Should delete comment")
    void shouldDeleteComment() throws Exception {
        doNothing().when(commentService).deleteComment(1L);

        mockMvc.perform(delete("/api/comments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should get comments by pin ID")
    void shouldGetCommentsByPinId() throws Exception {
        when(commentService.getCommentsByPinId(101L))
                .thenReturn(List.of(commentDto));

        mockMvc.perform(get("/api/comments/pin/101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].pinId").value(101L));
    }

    @Test
    @DisplayName("Should get comments by pin ID with pagination")
    void shouldGetCommentsByPinIdWithPagination() throws Exception {
        Page<CommentDto> page = new PageImpl<>(List.of(commentDto));
        when(commentService.getCommentsByPinId(eq(101L), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/comments/pin/101/paged"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].pinId").value(101L));
    }

    @Test
    @DisplayName("Should get comments by user ID")
    void shouldGetCommentsByUserId() throws Exception {
        when(commentService.getCommentsByUserId(201L))
                .thenReturn(List.of(commentDto));

        mockMvc.perform(get("/api/comments/user/201"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].userId").value(201L));
    }

    @Test
    @DisplayName("Should get comments by user ID with pagination")
    void shouldGetCommentsByUserIdWithPagination() throws Exception {
        Page<CommentDto> page = new PageImpl<>(List.of(commentDto));
        when(commentService.getCommentsByUserId(eq(201L), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/comments/user/201/paged"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].userId").value(201L));
    }
}