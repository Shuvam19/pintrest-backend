package com.pinterest.contentservice.repository;

import com.pinterest.contentservice.model.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    // Find boards by user ID
    List<Board> findByUserId(Long userId);
    
    // Find boards by user ID with pagination
    Page<Board> findByUserId(Long userId, Pageable pageable);
    
    // Find public boards by user ID
    List<Board> findByUserIdAndIsPrivate(Long userId, boolean isPrivate);
    
    // Find boards by title containing keyword
    List<Board> findByTitleContainingIgnoreCase(String keyword);
    
    // Find boards by category
    List<Board> findByCategory(String category);
    
    // Find boards by user ID ordered by display order
    List<Board> findByUserIdOrderByDisplayOrderAsc(Long userId);
    
    // Search boards by title or description
    @Query("SELECT b FROM Board b WHERE b.isPrivate = false AND (LOWER(b.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
            + "LOWER(b.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Board> searchBoards(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    // Find collaborative boards where user is a member
    @Query("SELECT b FROM Board b WHERE b.isCollaborative = true AND b.userId = :userId")
    List<Board> findCollaborativeBoardsByUserId(@Param("userId") Long userId);
    
    // Count boards by user ID
    long countByUserId(Long userId);
}