package com.pinterest.collaborationservice.repository;

import com.pinterest.collaborationservice.model.Invitation;
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
public class InvitationRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private InvitationRepository invitationRepository;

    private Invitation invitation1;
    private Invitation invitation2;

    @BeforeEach
    void setUp() {
        invitation1 = Invitation.builder()
                .senderId(101L)
                .receiverId(201L)
                .resourceId(301L)
                .resourceType(Invitation.ResourceType.BOARD)
                .message("Please join my board")
                .status(Invitation.InvitationStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        invitation2 = Invitation.builder()
                .senderId(101L)
                .receiverId(202L)
                .resourceId(302L)
                .resourceType(Invitation.ResourceType.BOARD)
                .message("Please join my other board")
                .status(Invitation.InvitationStatus.ACCEPTED)
                .createdAt(LocalDateTime.now())
                .build();

        entityManager.persist(invitation1);
        entityManager.persist(invitation2);
        entityManager.flush();
    }

    @Test
    @DisplayName("Should find invitations by sender ID")
    void shouldFindInvitationsBySenderId() {
        List<Invitation> invitations = invitationRepository.findBySenderId(101L);

        assertThat(invitations).isNotEmpty();
        assertThat(invitations).hasSize(2);
        assertThat(invitations.get(0).getSenderId()).isEqualTo(101L);
        assertThat(invitations.get(1).getSenderId()).isEqualTo(101L);
    }

    @Test
    @DisplayName("Should find invitations by receiver ID")
    void shouldFindInvitationsByReceiverId() {
        List<Invitation> invitations = invitationRepository.findByReceiverId(201L);

        assertThat(invitations).isNotEmpty();
        assertThat(invitations).hasSize(1);
        assertThat(invitations.get(0).getReceiverId()).isEqualTo(201L);
    }

    @Test
    @DisplayName("Should find invitations by receiver ID and status")
    void shouldFindInvitationsByReceiverIdAndStatus() {
        List<Invitation> invitations = invitationRepository.findByReceiverIdAndStatus(
                201L, Invitation.InvitationStatus.PENDING);

        assertThat(invitations).isNotEmpty();
        assertThat(invitations).hasSize(1);
        assertThat(invitations.get(0).getReceiverId()).isEqualTo(201L);
        assertThat(invitations.get(0).getStatus()).isEqualTo(Invitation.InvitationStatus.PENDING);
    }

    @Test
    @DisplayName("Should find invitations by sender ID with pagination")
    void shouldFindInvitationsBySenderIdWithPagination() {
        Page<Invitation> invitationsPage = invitationRepository.findBySenderId(
                101L, PageRequest.of(0, 10));

        assertThat(invitationsPage).isNotEmpty();
        assertThat(invitationsPage.getContent()).hasSize(2);
        assertThat(invitationsPage.getContent().get(0).getSenderId()).isEqualTo(101L);
        assertThat(invitationsPage.getContent().get(1).getSenderId()).isEqualTo(101L);
    }

    @Test
    @DisplayName("Should find invitations by receiver ID with pagination")
    void shouldFindInvitationsByReceiverIdWithPagination() {
        Page<Invitation> invitationsPage = invitationRepository.findByReceiverId(
                201L, PageRequest.of(0, 10));

        assertThat(invitationsPage).isNotEmpty();
        assertThat(invitationsPage.getContent()).hasSize(1);
        assertThat(invitationsPage.getContent().get(0).getReceiverId()).isEqualTo(201L);
    }

    @Test
    @DisplayName("Should find invitations by resource ID and type")
    void shouldFindInvitationsByResourceIdAndType() {
        List<Invitation> invitations = invitationRepository.findByResourceIdAndResourceType(
                301L, Invitation.ResourceType.BOARD);

        assertThat(invitations).isNotEmpty();
        assertThat(invitations).hasSize(1);
        assertThat(invitations.get(0).getResourceId()).isEqualTo(301L);
        assertThat(invitations.get(0).getResourceType()).isEqualTo(Invitation.ResourceType.BOARD);
    }

    @Test
    @DisplayName("Should find invitations by sender ID and status")
    void shouldFindInvitationsBySenderIdAndStatus() {
        List<Invitation> invitations = invitationRepository.findBySenderIdAndStatus(
                101L, Invitation.InvitationStatus.ACCEPTED);

        assertThat(invitations).isNotEmpty();
        assertThat(invitations).hasSize(1);
        assertThat(invitations.get(0).getSenderId()).isEqualTo(101L);
        assertThat(invitations.get(0).getStatus()).isEqualTo(Invitation.InvitationStatus.ACCEPTED);
    }
}