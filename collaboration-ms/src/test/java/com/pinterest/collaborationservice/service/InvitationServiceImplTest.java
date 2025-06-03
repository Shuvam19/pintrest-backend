package com.pinterest.collaborationservice.service;

import com.pinterest.collaborationservice.dto.InvitationDto;
import com.pinterest.collaborationservice.dto.InvitationRequest;
import com.pinterest.collaborationservice.dto.InvitationResponseRequest;
import com.pinterest.collaborationservice.exception.ResourceNotFoundException;
import com.pinterest.collaborationservice.model.Invitation;
import com.pinterest.collaborationservice.repository.InvitationRepository;
import com.pinterest.collaborationservice.service.impl.InvitationServiceImpl;
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
public class InvitationServiceImplTest {

    @Mock
    private InvitationRepository invitationRepository;

    @InjectMocks
    private InvitationServiceImpl invitationService;

    private Invitation invitation;
    private InvitationRequest invitationRequest;
    private InvitationResponseRequest responseRequest;

    @BeforeEach
    void setUp() {
        invitation = Invitation.builder()
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
    void shouldSendInvitation() {
        when(invitationRepository.save(any(Invitation.class))).thenReturn(invitation);

        InvitationDto result = invitationService.sendInvitation(invitationRequest);

        assertThat(result).isNotNull();
        assertThat(result.getSenderId()).isEqualTo(101L);
        assertThat(result.getReceiverId()).isEqualTo(201L);
        assertThat(result.getStatus()).isEqualTo(Invitation.InvitationStatus.PENDING);
        verify(invitationRepository, times(1)).save(any(Invitation.class));
    }

    @Test
    @DisplayName("Should get invitation by ID")
    void shouldGetInvitationById() {
        when(invitationRepository.findById(1L)).thenReturn(Optional.of(invitation));

        InvitationDto result = invitationService.getInvitationById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(invitationRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when invitation not found")
    void shouldThrowExceptionWhenInvitationNotFound() {
        when(invitationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            invitationService.getInvitationById(999L);
        });

        verify(invitationRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should respond to invitation")
    void shouldRespondToInvitation() {
        when(invitationRepository.findById(1L)).thenReturn(Optional.of(invitation));
        when(invitationRepository.save(any(Invitation.class))).thenReturn(invitation);

        InvitationDto result = invitationService.respondToInvitation(1L, responseRequest);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(Invitation.InvitationStatus.ACCEPTED);
        verify(invitationRepository, times(1)).findById(1L);
        verify(invitationRepository, times(1)).save(any(Invitation.class));
    }

    @Test
    @DisplayName("Should get sent invitations")
    void shouldGetSentInvitations() {
        when(invitationRepository.findBySenderId(101L)).thenReturn(List.of(invitation));

        List<InvitationDto> results = invitationService.getSentInvitations(101L);

        assertThat(results).isNotEmpty();
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getSenderId()).isEqualTo(101L);
        verify(invitationRepository, times(1)).findBySenderId(101L);
    }

    @Test
    @DisplayName("Should get received invitations")
    void shouldGetReceivedInvitations() {
        when(invitationRepository.findByReceiverId(201L)).thenReturn(List.of(invitation));

        List<InvitationDto> results = invitationService.getReceivedInvitations(201L);

        assertThat(results).isNotEmpty();
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getReceiverId()).isEqualTo(201L);
        verify(invitationRepository, times(1)).findByReceiverId(201L);
    }

    @Test
    @DisplayName("Should get pending invitations")
    void shouldGetPendingInvitations() {
        when(invitationRepository.findByReceiverIdAndStatus(201L, Invitation.InvitationStatus.PENDING))
                .thenReturn(List.of(invitation));

        List<InvitationDto> results = invitationService.getPendingInvitations(201L);

        assertThat(results).isNotEmpty();
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getStatus()).isEqualTo(Invitation.InvitationStatus.PENDING);
        verify(invitationRepository, times(1))
                .findByReceiverIdAndStatus(201L, Invitation.InvitationStatus.PENDING);
    }

    @Test
    @DisplayName("Should get sent invitations with pagination")
    void shouldGetSentInvitationsPaged() {
        Page<Invitation> page = new PageImpl<>(List.of(invitation));
        when(invitationRepository.findBySenderId(eq(101L), any(Pageable.class)))
                .thenReturn(page);

        Page<InvitationDto> results = invitationService.getSentInvitations(101L, Pageable.unpaged());

        assertThat(results).isNotEmpty();
        assertThat(results.getContent()).hasSize(1);
        assertThat(results.getContent().get(0).getSenderId()).isEqualTo(101L);
        verify(invitationRepository, times(1))
                .findBySenderId(eq(101L), any(Pageable.class));
    }

    @Test
    @DisplayName("Should get received invitations with pagination")
    void shouldGetReceivedInvitationsPaged() {
        Page<Invitation> page = new PageImpl<>(List.of(invitation));
        when(invitationRepository.findByReceiverId(eq(201L), any(Pageable.class)))
                .thenReturn(page);

        Page<InvitationDto> results = invitationService.getReceivedInvitations(201L, Pageable.unpaged());

        assertThat(results).isNotEmpty();
        assertThat(results.getContent()).hasSize(1);
        assertThat(results.getContent().get(0).getReceiverId()).isEqualTo(201L);
        verify(invitationRepository, times(1))
                .findByReceiverId(eq(201L), any(Pageable.class));
    }
}