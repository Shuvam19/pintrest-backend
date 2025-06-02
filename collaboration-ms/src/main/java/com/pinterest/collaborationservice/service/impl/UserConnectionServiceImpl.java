package com.pinterest.collaborationservice.service.impl;

import com.pinterest.collaborationservice.dto.ConnectionRequest;
import com.pinterest.collaborationservice.dto.UserConnectionDto;
import com.pinterest.collaborationservice.exception.ResourceNotFoundException;
import com.pinterest.collaborationservice.model.UserConnection;
import com.pinterest.collaborationservice.repository.UserConnectionRepository;
import com.pinterest.collaborationservice.service.UserConnectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserConnectionServiceImpl implements UserConnectionService {

    private final UserConnectionRepository userConnectionRepository;

    @Override
    @Transactional
    public UserConnectionDto createConnection(ConnectionRequest connectionRequest) {
        // Check if connection already exists
        if (userConnectionRepository.existsByFollowerIdAndFollowingId(
                connectionRequest.getFollowerId(), connectionRequest.getFollowingId())) {
            throw new IllegalStateException("Connection already exists");
        }
        
        // Create new connection
        UserConnection connection = UserConnection.builder()
                .followerId(connectionRequest.getFollowerId())
                .followingId(connectionRequest.getFollowingId())
                .status(UserConnection.ConnectionStatus.ACCEPTED) // Direct connections are accepted by default
                .note(connectionRequest.getNote())
                .notificationsEnabled(connectionRequest.isNotificationsEnabled())
                .build();
        
        UserConnection savedConnection = userConnectionRepository.save(connection);
        return mapToDto(savedConnection);
    }

    @Override
    public UserConnectionDto getConnection(Long followerId, Long followingId) {
        UserConnection connection = userConnectionRepository.findByFollowerIdAndFollowingId(followerId, followingId)
                .orElseThrow(() -> new ResourceNotFoundException("Connection not found"));
        return mapToDto(connection);
    }

    @Override
    @Transactional
    public UserConnectionDto updateConnectionStatus(Long connectionId, UserConnection.ConnectionStatus status) {
        UserConnection connection = userConnectionRepository.findById(connectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Connection not found with id: " + connectionId));
        
        connection.setStatus(status);
        UserConnection updatedConnection = userConnectionRepository.save(connection);
        return mapToDto(updatedConnection);
    }

    @Override
    @Transactional
    public void deleteConnection(Long connectionId) {
        UserConnection connection = userConnectionRepository.findById(connectionId)
                .orElseThrow(() -> new ResourceNotFoundException("Connection not found with id: " + connectionId));
        
        userConnectionRepository.delete(connection);
    }

    @Override
    public List<UserConnectionDto> getFollowing(Long userId) {
        List<UserConnection> connections = userConnectionRepository.findByFollowerIdAndStatus(
                userId, UserConnection.ConnectionStatus.ACCEPTED);
        return connections.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public List<UserConnectionDto> getFollowers(Long userId) {
        List<UserConnection> connections = userConnectionRepository.findByFollowingIdAndStatus(
                userId, UserConnection.ConnectionStatus.ACCEPTED);
        return connections.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public Page<UserConnectionDto> getFollowing(Long userId, Pageable pageable) {
        Page<UserConnection> connections = userConnectionRepository.findByFollowerIdAndStatus(
                userId, UserConnection.ConnectionStatus.ACCEPTED, pageable);
        return connections.map(this::mapToDto);
    }

    @Override
    public Page<UserConnectionDto> getFollowers(Long userId, Pageable pageable) {
        Page<UserConnection> connections = userConnectionRepository.findByFollowingIdAndStatus(
                userId, UserConnection.ConnectionStatus.ACCEPTED, pageable);
        return connections.map(this::mapToDto);
    }

    @Override
    public long getFollowersCount(Long userId) {
        return userConnectionRepository.countByFollowingIdAndStatus(
                userId, UserConnection.ConnectionStatus.ACCEPTED);
    }

    @Override
    public long getFollowingCount(Long userId) {
        return userConnectionRepository.countByFollowerIdAndStatus(
                userId, UserConnection.ConnectionStatus.ACCEPTED);
    }

    @Override
    public boolean isFollowing(Long followerId, Long followingId) {
        return userConnectionRepository.existsByFollowerIdAndFollowingIdAndStatus(
                followerId, followingId, UserConnection.ConnectionStatus.ACCEPTED);
    }

    @Override
    public List<UserConnectionDto> getMutualConnections(Long userId) {
        List<UserConnection> connections = userConnectionRepository.findMutualConnections(userId);
        return connections.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public Page<UserConnectionDto> getMutualConnections(Long userId, Pageable pageable) {
        Page<UserConnection> connections = userConnectionRepository.findMutualConnections(userId, pageable);
        return connections.map(this::mapToDto);
    }

    @Override
    @Transactional
    public UserConnectionDto blockUser(Long userId, Long userToBlockId) {
        // Check if there's an existing connection
        UserConnection connection = userConnectionRepository.findByFollowerIdAndFollowingId(userId, userToBlockId)
                .orElse(null);
        
        if (connection == null) {
            // Create a new connection with BLOCKED status
            connection = UserConnection.builder()
                    .followerId(userId)
                    .followingId(userToBlockId)
                    .status(UserConnection.ConnectionStatus.BLOCKED)
                    .build();
        } else {
            // Update existing connection to BLOCKED
            connection.setStatus(UserConnection.ConnectionStatus.BLOCKED);
        }
        
        UserConnection savedConnection = userConnectionRepository.save(connection);
        return mapToDto(savedConnection);
    }

    @Override
    @Transactional
    public void unblockUser(Long userId, Long blockedUserId) {
        UserConnection connection = userConnectionRepository.findByFollowerIdAndFollowingId(userId, blockedUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Block relationship not found"));
        
        if (connection.getStatus() == UserConnection.ConnectionStatus.BLOCKED) {
            userConnectionRepository.delete(connection);
        } else {
            throw new IllegalStateException("User is not blocked");
        }
    }

    @Override
    public List<UserConnectionDto> getBlockedUsers(Long userId) {
        List<UserConnection> connections = userConnectionRepository.findByFollowerIdAndStatus(
                userId, UserConnection.ConnectionStatus.BLOCKED);
        return connections.stream().map(this::mapToDto).collect(Collectors.toList());
    }
    
    // Helper method to map entity to DTO
    private UserConnectionDto mapToDto(UserConnection connection) {
        return UserConnectionDto.builder()
                .id(connection.getId())
                .followerId(connection.getFollowerId())
                .followingId(connection.getFollowingId())
                .status(connection.getStatus())
                .note(connection.getNote())
                .notificationsEnabled(connection.isNotificationsEnabled())
                .createdAt(connection.getCreatedAt())
                .updatedAt(connection.getUpdatedAt())
                // Additional fields would be populated from User service in a real implementation
                .build();
    }
}