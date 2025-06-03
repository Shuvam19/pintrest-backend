package com.pinterest.collaborationservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pinterest.collaborationservice.dto.BoardCollaborationDto;
import com.pinterest.collaborationservice.model.BoardCollaboration;
import com.pinterest.collaborationservice.service.BoardCollaborationService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BoardCollaborationController.class)
public class BoardCollaborationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BoardCollaborationService boardCollaborationService;

    @Autowired
    private ObjectMapper objectMapper;

    private BoardCollaborationDto collaborationDto;

    @BeforeEach
    void setUp() {
        collaborationDto = BoardCollaborationDto.builder()
                .id(1L)
                .boardId(101L)
                .userId(201L)
                .invitedBy(301L)
                .permissionLevel(BoardCollaboration.PermissionLevel.EDIT)
                .status(BoardCollaboration.CollaborationStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should add collaborator successfully")
    void shouldAddCollaborator() throws Exception {
        when(boardCollaborationService.addCollaborator(
                eq(101L), eq(201L), eq(301L), eq(BoardCollaboration.PermissionLevel.EDIT)))
                .thenReturn(collaborationDto);

        mockMvc.perform(post("/api/board-collaborations")
                .param("boardId", "101")
                .param("userId", "201")
                .param("invitedBy", "301")
                .param("permissionLevel", "EDIT")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.boardId").value(101))
                .andExpect(jsonPath("$.data.userId").value(201));
    }

    @Test
    @DisplayName("Should get collaboration by board ID and user ID")
    void shouldGetCollaboration() throws Exception {
        when(boardCollaborationService.getCollaboration(101L, 201L))
                .thenReturn(collaborationDto);

        mockMvc.perform(get("/api/board-collaborations/101/201"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.boardId").value(101))
                .andExpect(jsonPath("$.data.userId").value(201));
    }

    @Test
    @DisplayName("Should update collaboration status")
    void shouldUpdateCollaborationStatus() throws Exception {
        collaborationDto.setStatus(BoardCollaboration.CollaborationStatus.INACTIVE);
        when(boardCollaborationService.updateCollaborationStatus(
                eq(1L), eq(BoardCollaboration.CollaborationStatus.INACTIVE)))
                .thenReturn(collaborationDto);

        mockMvc.perform(put("/api/board-collaborations/1/status")
                .param("status", "INACTIVE")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("INACTIVE"));
    }

    @Test
    @DisplayName("Should update permission level")
    void shouldUpdatePermissionLevel() throws Exception {
        collaborationDto.setPermissionLevel(BoardCollaboration.PermissionLevel.VIEW);
        when(boardCollaborationService.updatePermissionLevel(
                eq(1L), eq(BoardCollaboration.PermissionLevel.VIEW)))
                .thenReturn(collaborationDto);

        mockMvc.perform(put("/api/board-collaborations/1/permission")
                .param("permissionLevel", "VIEW")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.permissionLevel").value("VIEW"));
    }

    @Test
    @DisplayName("Should get board collaborators")
    void shouldGetBoardCollaborators() throws Exception {
        when(boardCollaborationService.getBoardCollaborators(101L))
                .thenReturn(List.of(collaborationDto));

        mockMvc.perform(get("/api/board-collaborations/board/101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].boardId").value(101));
    }

    @Test
    @DisplayName("Should get user collaborations")
    void shouldGetUserCollaborations() throws Exception {
        when(boardCollaborationService.getUserCollaborations(201L))
                .thenReturn(List.of(collaborationDto));

        mockMvc.perform(get("/api/board-collaborations/user/201"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].userId").value(201));
    }

    @Test
    @DisplayName("Should get board collaborators with pagination")
    void shouldGetBoardCollaboratorsPaged() throws Exception {
        Page<BoardCollaborationDto> page = new PageImpl<>(List.of(collaborationDto));
        when(boardCollaborationService.getBoardCollaborators(eq(101L), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/board-collaborations/board/101/paged"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].boardId").value(101));
    }
}