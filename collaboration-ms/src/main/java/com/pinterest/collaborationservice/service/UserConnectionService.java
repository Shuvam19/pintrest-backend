package com.pinterest.collaborationservice.service;

import com.pinterest.collaborationservice.dto.ConnectionRequest;
import com.pinterest.collaborationservice.dto.UserConnectionDto;
import com.pinterest.collaborationservice.model.UserConnection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserConnectionService {

    // Create a new connection (follow)
    UserConnectionDto createConnection(ConnectionRequest connectionRequest);
    
    // Get a specific connection
    UserConnectionDto getConnection(Long followerId, Long followingId);
    
    // Update connection status (accept, block)
    UserConnectionDto updateConnectionStatus(Long connectionId, UserConnection.ConnectionStatus status);
    
    // Delete a connection (unfollow)
    void deleteConnection(Long connectionId);
    
    // Get all users that a specific user is following
    List<UserConnectionDto> getFollowing(Long userId);
    
    // Get all users that are following a specific user
    List<UserConnectionDto> getFollowers(Long userId);
    
    // Get paginated list of users that a specific user is following
    Page<UserConnectionDto> getFollowing(Long userId, Pageable pageable);
    
    // Get paginated list of users that are following a specific user
    Page<UserConnectionDto> getFollowers(Long userId, Pageable pageable);
    
    // Get count of followers
    long getFollowersCount(Long userId);
    
    // Get count of following
    long getFollowingCount(Long userId);
    
    // Check if a user is following another user
    boolean isFollowing(Long followerId, Long followingId);
    
    // Get mutual connections (users that both follow each other)
    List<UserConnectionDto> getMutualConnections(Long userId);
    
    // Get paginated list of mutual connections
    Page<UserConnectionDto> getMutualConnections(Long userId, Pageable pageable);
    
    // Block a user
    UserConnectionDto blockUser(Long userId, Long userToBlockId);
    
    // Unblock a user
    void unblockUser(Long userId, Long blockedUserId);
    
    // Get all blocked users
    List<UserConnectionDto> getBlockedUsers(Long userId);
}