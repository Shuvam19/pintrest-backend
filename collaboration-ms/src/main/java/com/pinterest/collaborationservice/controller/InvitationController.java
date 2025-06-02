package com.pinterest.collaborationservice.controller;

import com.pinterest.collaborationservice.dto.ApiResponse;
import com.pinterest.collaborationservice.dto.InvitationDto;
import com.pinterest.collaborationservice.dto.InvitationRequest;
import com.pinterest.collaborationservice.dto.InvitationResponseRequest;
import com.pinterest.collaborationservice.service.InvitationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/invitations")
@RequiredArgsConstructor
public class InvitationController {

    private final InvitationService invitationService;

    @PostMapping
    public ResponseEntity<ApiResponse<InvitationDto>> createInvitation(
            @Valid @RequestBody InvitationRequest request) {
        InvitationDto invitation = invitationService.createInvitation(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Invitation created successfully", invitation));
    }

    @GetMapping("/{invitationId}")
    public ResponseEntity<ApiResponse<InvitationDto>> getInvitation(
            @PathVariable Long invitationId) {
        InvitationDto invitation = invitationService.getInvitation(invitationId);
        return ResponseEntity.ok(ApiResponse.success("Invitation retrieved successfully", invitation));
    }

    @PostMapping("/respond")
    public ResponseEntity<ApiResponse<InvitationDto>> respondToInvitation(
            @Valid @RequestBody InvitationResponseRequest request) {
        InvitationDto invitation = invitationService.respondToInvitation(request);
        return ResponseEntity.ok(ApiResponse.success("Invitation response processed successfully", invitation));
    }

    @GetMapping("/sent/{senderId}")
    public ResponseEntity<ApiResponse<Page<InvitationDto>>> getSentInvitations(
            @PathVariable Long senderId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<InvitationDto> invitations = invitationService.getSentInvitations(senderId, pageable);
        return ResponseEntity.ok(ApiResponse.success("Sent invitations retrieved successfully", invitations));
    }

    @GetMapping("/received/{recipientId}")
    public ResponseEntity<ApiResponse<Page<InvitationDto>>> getReceivedInvitations(
            @PathVariable Long recipientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<InvitationDto> invitations = invitationService.getReceivedInvitations(recipientId, pageable);
        return ResponseEntity.ok(ApiResponse.success("Received invitations retrieved successfully", invitations));
    }

    @GetMapping("/pending/{recipientId}")
    public ResponseEntity<ApiResponse<Page<InvitationDto>>> getPendingInvitations(
            @PathVariable Long recipientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<InvitationDto> invitations = invitationService.getPendingInvitations(recipientId, pageable);
        return ResponseEntity.ok(ApiResponse.success("Pending invitations retrieved successfully", invitations));
    }

    @GetMapping("/type/{recipientId}/{type}")
    public ResponseEntity<ApiResponse<Page<InvitationDto>>> getInvitationsByType(
            @PathVariable Long recipientId,
            @PathVariable String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<InvitationDto> invitations = invitationService.getInvitationsByType(recipientId, type, pageable);
        return ResponseEntity.ok(ApiResponse.success("Invitations by type retrieved successfully", invitations));
    }

    @GetMapping("/pending/{recipientId}/all")
    public ResponseEntity<ApiResponse<List<InvitationDto>>> getAllPendingInvitations(
            @PathVariable Long recipientId) {
        List<InvitationDto> invitations = invitationService.getPendingInvitations(recipientId);
        return ResponseEntity.ok(ApiResponse.success("All pending invitations retrieved successfully", invitations));
    }

    @GetMapping("/count/pending/{recipientId}")
    public ResponseEntity<ApiResponse<Long>> countPendingInvitations(
            @PathVariable Long recipientId) {
        long count = invitationService.countPendingInvitations(recipientId);
        return ResponseEntity.ok(ApiResponse.success("Pending invitations count retrieved successfully", count));
    }

    @PostMapping("/cancel/{invitationId}/{senderId}")
    public ResponseEntity<ApiResponse<InvitationDto>> cancelInvitation(
            @PathVariable Long invitationId,
            @PathVariable Long senderId) {
        InvitationDto invitation = invitationService.cancelInvitation(invitationId, senderId);
        return ResponseEntity.ok(ApiResponse.success("Invitation canceled successfully", invitation));
    }

    @DeleteMapping("/{invitationId}")
    public ResponseEntity<ApiResponse<Void>> deleteInvitation(
            @PathVariable Long invitationId) {
        invitationService.deleteInvitation(invitationId);
        return ResponseEntity.ok(ApiResponse.success("Invitation deleted successfully", null));
    }

    @PostMapping("/process-expired")
    public ResponseEntity<ApiResponse<Void>> processExpiredInvitations() {
        invitationService.processExpiredInvitations();
        return ResponseEntity.ok(ApiResponse.success("Expired invitations processed successfully", null));
    }
}