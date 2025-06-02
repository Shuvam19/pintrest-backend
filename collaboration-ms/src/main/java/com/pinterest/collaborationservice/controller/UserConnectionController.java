package com.pinterest.collaborationservice.controller;

import com.pinterest.collaborationservice.dto.ApiResponse;
import com.pinterest.collaborationservice.dto.ConnectionRequest;
import com.pinterest.collaborationservice.dto.UserConnectionDto;
import com.pinterest.collaborationservice.model.UserConnection;
import com.pinterest.collaborationservice.service.UserConnectionService;
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
@RequestMapping("/api/connections")
@RequiredArgsConstructor
public class UserConnectionController {

    private final UserConnectionService userConnectionService;

    @PostMapping
    public ResponseEntity<ApiResponse<UserConnectionDto>> createConnection(
            @Valid @RequestBody ConnectionRequest request) {
        UserConnectionDto connection = userConnectionService.createConnection(
                request.getFollowerId(),
                request.getFollowingId(),
                request.getNote(),
                request.isNotificationsEnabled());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Connection created successfully", connection));
    }

    @GetMapping("/{connectionId}")
    public ResponseEntity<ApiResponse<UserConnectionDto>> getConnection(
            @PathVariable Long connectionId) {
        UserConnectionDto connection = userConnectionService.getConnection(connectionId);
        return ResponseEntity.ok(ApiResponse.success("Connection retrieved successfully", connection));
    }

    @PutMapping("/{connectionId}/status")
    public ResponseEntity<ApiResponse<UserConnectionDto>> updateConnectionStatus(
            @PathVariable Long connectionId,
            @RequestParam String status) {
        UserConnectionDto connection = userConnectionService.updateConnectionStatus(
                connectionId, UserConnection.ConnectionStatus.valueOf(status));
        return ResponseEntity.ok(ApiResponse.success("Connection status updated successfully", connection));
    }

    @DeleteMapping("/{connectionId}")
    public ResponseEntity<ApiResponse<Void>> deleteConnection(
            @PathVariable Long connectionId) {
        userConnectionService.deleteConnection(connectionId);
        return ResponseEntity.ok(ApiResponse.success("Connection deleted successfully", null));
    }

    @GetMapping("/followers/{userId}")
    public ResponseEntity<ApiResponse<List<UserConnectionDto>>> getFollowers(
            @PathVariable Long userId) {
        List<UserConnectionDto> followers = userConnectionService.getFollowers(userId);
        return ResponseEntity.ok(ApiResponse.success("Followers retrieved successfully", followers));
    }

    @GetMapping("/following/{userId}")
    public ResponseEntity<ApiResponse<List<UserConnectionDto>>> getFollowing(
            @PathVariable Long userId) {
        List<UserConnectionDto> following = userConnectionService.getFollowing(userId);
        return ResponseEntity.ok(ApiResponse.success("Following retrieved successfully", following));
    }

    @GetMapping("/followers/{userId}/paged")
    public ResponseEntity<ApiResponse<Page<UserConnectionDto>>> getFollowersPaged(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<UserConnectionDto> followers = userConnectionService.getFollowersPaged(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success("Followers retrieved successfully", followers));
    }

    @GetMapping("/following/{userId}/paged")
    public ResponseEntity<ApiResponse<Page<UserConnectionDto>>> getFollowingPaged(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<UserConnectionDto> following = userConnectionService.getFollowingPaged(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success("Following retrieved successfully", following));
    }

    @GetMapping("/count/followers/{userId}")
    public ResponseEntity<ApiResponse<Long>> countFollowers(
            @PathVariable Long userId) {
        long count = userConnectionService.countFollowers(userId);
        return ResponseEntity.ok(ApiResponse.success("Followers count retrieved successfully", count));
    }

    @GetMapping("/count/following/{userId}")
    public ResponseEntity<ApiResponse<Long>> countFollowing(
            @PathVariable Long userId) {
        long count = userConnectionService.countFollowing(userId);
        return ResponseEntity.ok(ApiResponse.success("Following count retrieved successfully", count));
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<Boolean>> checkFollowStatus(
            @RequestParam Long followerId,
            @RequestParam Long followingId) {
        boolean isFollowing = userConnectionService.isFollowing(followerId, followingId);
        return ResponseEntity.ok(ApiResponse.success("Follow status retrieved successfully", isFollowing));
    }

    @GetMapping("/mutual/{userId1}/{userId2}")
    public ResponseEntity<ApiResponse<List<UserConnectionDto>>> getMutualConnections(
            @PathVariable Long userId1,
            @PathVariable Long userId2) {
        List<UserConnectionDto> mutualConnections = userConnectionService.getMutualConnections(userId1, userId2);
        return ResponseEntity.ok(ApiResponse.success("Mutual connections retrieved successfully", mutualConnections));
    }

    @GetMapping("/mutual/{userId1}/{userId2}/paged")
    public ResponseEntity<ApiResponse<Page<UserConnectionDto>>> getMutualConnectionsPaged(
            @PathVariable Long userId1,
            @PathVariable Long userId2,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserConnectionDto> mutualConnections = userConnectionService.getMutualConnectionsPaged(userId1, userId2, pageable);
        return ResponseEntity.ok(ApiResponse.success("Mutual connections retrieved successfully", mutualConnections));
    }

    @PostMapping("/block/{userId}/{blockedUserId}")
    public ResponseEntity<ApiResponse<Void>> blockUser(
            @PathVariable Long userId,
            @PathVariable Long blockedUserId) {
        userConnectionService.blockUser(userId, blockedUserId);
        return ResponseEntity.ok(ApiResponse.success("User blocked successfully", null));
    }

    @PostMapping("/unblock/{userId}/{blockedUserId}")
    public ResponseEntity<ApiResponse<Void>> unblockUser(
            @PathVariable Long userId,
            @PathVariable Long blockedUserId) {
        userConnectionService.unblockUser(userId, blockedUserId);
        return ResponseEntity.ok(ApiResponse.success("User unblocked successfully", null));
    }
}