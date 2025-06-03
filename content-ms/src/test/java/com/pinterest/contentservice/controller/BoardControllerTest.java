package com.pinterest.contentservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pinterest.contentservice.dto.BoardDto;
import com.pinterest.contentservice.dto.BoardRequest;
import com.pinterest.contentservice.service.BoardService;
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

@WebMvcTest(BoardController.class)
public class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BoardService boardService;

    @Autowired
    private ObjectMapper objectMapper;

    private BoardDto boardDto;
    private BoardRequest boardRequest;

    @BeforeEach
    void setUp() {
        boardDto = BoardDto.builder()
                .id(1L)
                .name("Test Board")
                .description("Test Description")
                .coverImageUrl("https://example.com/cover.jpg")
                .userId(101L)
                .isPrivate(false)
                .createdAt(LocalDateTime.now())
                .build();

        boardRequest = BoardRequest.builder()
                .name("Test Board")
                .description("Test Description")
                .coverImageUrl("https://example.com/cover.jpg")
                .userId(101L)
                .isPrivate(false)
                .build();
    }

    @Test
    @DisplayName("Should create board successfully")
    void shouldCreateBoard() throws Exception {
        when(boardService.createBoard(any(BoardRequest.class)))
                .thenReturn(boardDto);

        mockMvc.perform(post("/api/content/boards")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(boardRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.name").value("Test Board"));
    }

    @Test
    @DisplayName("Should get board by ID")
    void shouldGetBoardById() throws Exception {
        when(boardService.getBoardById(1L))
                .thenReturn(boardDto);

        mockMvc.perform(get("/api/content/boards/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.name").value("Test Board"));
    }

    @Test
    @DisplayName("Should update board successfully")
    void shouldUpdateBoard() throws Exception {
        when(boardService.updateBoard(eq(1L), any(BoardRequest.class)))
                .thenReturn(boardDto);

        mockMvc.perform(put("/api/content/boards/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(boardRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1L));
    }

    @Test
    @DisplayName("Should delete board successfully")
    void shouldDeleteBoard() throws Exception {
        doNothing().when(boardService).deleteBoard(1L);

        mockMvc.perform(delete("/api/content/boards/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should get boards by user ID")
    void shouldGetBoardsByUserId() throws Exception {
        when(boardService.getBoardsByUserId(101L))
                .thenReturn(List.of(boardDto));

        mockMvc.perform(get("/api/content/boards/user/101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].userId").value(101L));
    }

    @Test
    @DisplayName("Should get boards by user ID with pagination")
    void shouldGetBoardsByUserIdPaged() throws Exception {
        Page<BoardDto> page = new PageImpl<>(List.of(boardDto));
        when(boardService.getBoardsByUserId(eq(101L), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/content/boards/user/101/paged"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].userId").value(101L));
    }

    @Test
    @DisplayName("Should get public boards")
    void shouldGetPublicBoards() throws Exception {
        Page<BoardDto> page = new PageImpl<>(List.of(boardDto));
        when(boardService.getPublicBoards(any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/content/boards/public"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].isPrivate").value(false));
    }
}