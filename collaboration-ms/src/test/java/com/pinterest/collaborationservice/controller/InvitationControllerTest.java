package com.pinterest.collaborationservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pinterest.collaborationservice.dto.InvitationDto;
import com.pinterest.collaborationservice.dto.InvitationRequest;
import com.pinterest.collaborationservice.dto.InvitationResponseRequest;
import com.pinterest.collaborationservice.model.Invitation;
import com.pinterest.collaborationservice.service.InvitationService;
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

@WebMvcTest(InvitationController.class)
public class InvitationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InvitationService invitationService;

    @Autowired
    private ObjectMapper objectMapper;

    private InvitationDto invitationDto;
    private InvitationRequest invitationRequest;
    private InvitationResponseRequest responseRequest;

    @BeforeEach
    void setUp() {
        invitationDto = InvitationDto.builder()
                .id(1L)
                .senderId(101L)
                .receiverId(201L)
                .resourceId(301L)
                .resourceType(Invitation.ResourceType.BOARD)
                .message("Please join my board")
                .status(Invitation.InvitationStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        invitationRequest = InvitationRequest.builder()
                .senderId(101L)
                .receiverId(201L)
                .resourceId(301L)
                .resourceType(Invitation.ResourceType.BOARD)
                .message("Please join my board")
                .build();

        responseRequest = InvitationResponseRequest.builder()
                .status(Invitation.InvitationStatus.ACCEPTED)
                .build();
    }

    @Test
    @DisplayName("Should send invitation successfully")
    void shouldSendInvitation() throws Exception {
        when(invitationService.sendInvitation(any(InvitationRequest.class)))
                .thenReturn(invitationDto);

        mockMvc.perform(post("/api/invitations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invitationRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.senderId").value(101L))
                .andExpect(jsonPath("$.data.receiverId").value(201L));
    }

    @Test
    @DisplayName("Should get invitation by ID")
    void shouldGetInvitationById() throws Exception {
        when(invitationService.getInvitationById(1L))
                .thenReturn(invitationDto);

        mockMvc.perform(get("/api/invitations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1L));
    }

    @Test
    @DisplayName("Should respond to invitation")
    void shouldRespondToInvitation() throws Exception {
        invitationDto.setStatus(Invitation.InvitationStatus.ACCEPTED);
        when(invitationService.respondToInvitation(eq(1L), any(InvitationResponseRequest.class)))
                .thenReturn(invitationDto);

        mockMvc.perform(put("/api/invitations/1/respond")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(responseRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("ACCEPTED"));
    }

    @Test
    @DisplayName("Should get sent invitations")
    void shouldGetSentInvitations() throws Exception {
        when(invitationService.getSentInvitations(101L))
                .thenReturn(List.of(invitationDto));

        mockMvc.perform(get("/api/invitations/sent/101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].senderId").value(101L));
    }

    @Test
    @DisplayName("Should get received invitations")
    void shouldGetReceivedInvitations() throws Exception {
        when(invitationService.getReceivedInvitations(201L))
                .thenReturn(List.of(invitationDto));

        mockMvc.perform(get("/api/invitations/received/201"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].receiverId").value(201L));
    }

    @Test
    @DisplayName("Should get pending invitations")
    void shouldGetPendingInvitations() throws Exception {
        when(invitationService.getPendingInvitations(201L))
                .thenReturn(List.of(invitationDto));

        mockMvc.perform(get("/api/invitations/pending/201"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].status").value("PENDING"));
    }

    @Test
    @DisplayName("Should get sent invitations with pagination")
    void shouldGetSentInvitationsPaged() throws Exception {
        Page<InvitationDto> page = new PageImpl<>(List.of(invitationDto));
        when(invitationService.getSentInvitations(eq(101L), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/invitations/sent/101/paged"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].senderId").value(101L));
    }
}