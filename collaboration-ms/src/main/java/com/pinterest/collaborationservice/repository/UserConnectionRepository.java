package com.pinterest.collaborationservice.repository;

import com.pinterest.collaborationservice.model.UserConnection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserConnectionRepository extends JpaRepository<UserConnection, Long> {

    // Find a specific connection between two users
    Optional<UserConnection> findByFollowerIdAndFollowingId(Long followerId, Long followingId);
    
    // Find all users that a specific user is following
    List<UserConnection> findByFollowerIdAndStatus(Long followerId, UserConnection.ConnectionStatus status);
    
    // Find all users that are following a specific user
    List<UserConnection> findByFollowingIdAndStatus(Long followingId, UserConnection.ConnectionStatus status);
    
    // Paginated version for following
    Page<UserConnection> findByFollowerIdAndStatus(Long followerId, UserConnection.ConnectionStatus status, Pageable pageable);
    
    // Paginated version for followers
    Page<UserConnection> findByFollowingIdAndStatus(Long followingId, UserConnection.ConnectionStatus status, Pageable pageable);
    
    // Count followers
    long countByFollowingIdAndStatus(Long followingId, UserConnection.ConnectionStatus status);
    
    // Count following
    long countByFollowerIdAndStatus(Long followerId, UserConnection.ConnectionStatus status);
    
    // Check if a connection exists with any status
    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);
    
    // Check if a connection exists with a specific status
    boolean existsByFollowerIdAndFollowingIdAndStatus(Long followerId, Long followingId, UserConnection.ConnectionStatus status);
    
    // Find mutual connections (users that both follow each other)
    @Query("SELECT uc FROM UserConnection uc WHERE uc.followerId = :userId AND uc.status = 'ACCEPTED' AND uc.followingId IN " +
           "(SELECT uc2.followerId FROM UserConnection uc2 WHERE uc2.followingId = :userId AND uc2.status = 'ACCEPTED')")
    List<UserConnection> findMutualConnections(@Param("userId") Long userId);
    
    // Find mutual connections paginated
    @Query("SELECT uc FROM UserConnection uc WHERE uc.followerId = :userId AND uc.status = 'ACCEPTED' AND uc.followingId IN " +
           "(SELECT uc2.followerId FROM UserConnection uc2 WHERE uc2.followingId = :userId AND uc2.status = 'ACCEPTED')")
    Page<UserConnection> findMutualConnections(@Param("userId") Long userId, Pageable pageable);
}