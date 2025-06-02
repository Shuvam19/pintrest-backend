package com.pinterest.collaborationservice.controller;

import com.pinterest.collaborationservice.dto.ApiResponse;
import com.pinterest.collaborationservice.dto.BoardCollaborationDto;
import com.pinterest.collaborationservice.model.BoardCollaboration;
import com.pinterest.collaborationservice.service.BoardCollaborationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/board-collaborations")
@RequiredArgsConstructor
public class BoardCollaborationController {

    private final BoardCollaborationService boardCollaborationService;

    @PostMapping
    public ResponseEntity<ApiResponse<BoardCollaborationDto>> addCollaborator(
            @RequestParam Long boardId,
            @RequestParam Long userId,
            @RequestParam Long invitedBy,
            @RequestParam String permissionLevel) {
        BoardCollaborationDto collaboration = boardCollaborationService.addCollaborator(
                boardId, userId, invitedBy, BoardCollaboration.PermissionLevel.valueOf(permissionLevel));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Collaborator added successfully", collaboration));
    }

    @GetMapping("/{boardId}/{userId}")
    public ResponseEntity<ApiResponse<BoardCollaborationDto>> getCollaboration(
            @PathVariable Long boardId,
            @PathVariable Long userId) {
        BoardCollaborationDto collaboration = boardCollaborationService.getCollaboration(boardId, userId);
        return ResponseEntity.ok(ApiResponse.success("Collaboration retrieved successfully", collaboration));
    }

    @PutMapping("/{collaborationId}/status")
    public ResponseEntity<ApiResponse<BoardCollaborationDto>> updateCollaborationStatus(
            @PathVariable Long collaborationId,
            @RequestParam String status) {
        BoardCollaborationDto collaboration = boardCollaborationService.updateCollaborationStatus(
                collaborationId, BoardCollaboration.CollaborationStatus.valueOf(status));
        return ResponseEntity.ok(ApiResponse.success("Collaboration status updated successfully", collaboration));
    }

    @PutMapping("/{collaborationId}/permission")
    public ResponseEntity<ApiResponse<BoardCollaborationDto>> updatePermissionLevel(
            @PathVariable Long collaborationId,
            @RequestParam String permissionLevel) {
        BoardCollaborationDto collaboration = boardCollaborationService.updatePermissionLevel(
                collaborationId, BoardCollaboration.PermissionLevel.valueOf(permissionLevel));
        return ResponseEntity.ok(ApiResponse.success("Permission level updated successfully", collaboration));
    }

    @DeleteMapping("/{collaborationId}")
    public ResponseEntity<ApiResponse<Void>> removeCollaborator(
            @PathVariable Long collaborationId) {
        boardCollaborationService.removeCollaborator(collaborationId);
        return ResponseEntity.ok(ApiResponse.success("Collaborator removed successfully", null));
    }

    @GetMapping("/board/{boardId}")
    public ResponseEntity<ApiResponse<List<BoardCollaborationDto>>> getBoardCollaborators(
            @PathVariable Long boardId) {
        List<BoardCollaborationDto> collaborators = boardCollaborationService.getBoardCollaborators(boardId);
        return ResponseEntity.ok(ApiResponse.success("Board collaborators retrieved successfully", collaborators));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<BoardCollaborationDto>>> getUserCollaborations(
            @PathVariable Long userId) {
        List<BoardCollaborationDto> collaborations = boardCollaborationService.getUserCollaborations(userId);
        return ResponseEntity.ok(ApiResponse.success("User collaborations retrieved successfully", collaborations));
    }

    @GetMapping("/board/{boardId}/paged")
    public ResponseEntity<ApiResponse<Page<BoardCollaborationDto>>> getBoardCollaboratorsPaged(
            @PathVariable Long boardId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<BoardCollaborationDto> collaborators = boardCollaborationService.getBoardCollaborators(boardId, pageable);
        return ResponseEntity.ok(ApiResponse.success("Board collaborators retrieved successfully", collaborators));
    }

    @GetMapping("/user/{userId}/paged")
    public ResponseEntity<ApiResponse<Page<BoardCollaborationDto>>> getUserCollaborationsPaged(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<BoardCollaborationDto> collaborations = boardCollaborationService.getUserCollaborations(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success("User collaborations retrieved successfully", collaborations));
    }

    @GetMapping("/count/{boardId}")
    public ResponseEntity<ApiResponse<Long>> getCollaboratorsCount(
            @PathVariable Long boardId) {
        long count = boardCollaborationService.getCollaboratorsCount(boardId);
        return ResponseEntity.ok(ApiResponse.success("Collaborators count retrieved successfully", count));
    }

    @GetMapping("/check")
    public ResponseEntity<ApiResponse<Boolean>> isCollaborator(
            @RequestParam Long boardId,
            @RequestParam Long userId) {
        boolean isCollaborator = boardCollaborationService.isCollaborator(boardId, userId);
        return ResponseEntity.ok(ApiResponse.success("Collaboration status retrieved successfully", isCollaborator));
    }

    @GetMapping("/permission")
    public ResponseEntity<ApiResponse<Boolean>> hasPermission(
            @RequestParam Long boardId,
            @RequestParam Long userId,
            @RequestParam String permissionLevel) {
        boolean hasPermission = boardCollaborationService.hasPermission(
                boardId, userId, BoardCollaboration.PermissionLevel.valueOf(permissionLevel));
        return ResponseEntity.ok(ApiResponse.success("Permission status retrieved successfully", hasPermission));
    }

    @GetMapping("/admin/{userId}")
    public ResponseEntity<ApiResponse<List<BoardCollaborationDto>>> getAdminBoards(
            @PathVariable Long userId) {
        List<BoardCollaborationDto> adminBoards = boardCollaborationService.getAdminBoards(userId);
        return ResponseEntity.ok(ApiResponse.success("Admin boards retrieved successfully", adminBoards));
    }
}