package com.pinterest.contentservice.repository;

import com.pinterest.contentservice.model.Pin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PinRepository extends JpaRepository<Pin, Long> {

    // Find pins by user ID
    List<Pin> findByUserId(Long userId);
    
    // Find pins by user ID with pagination
    Page<Pin> findByUserId(Long userId, Pageable pageable);
    
    // Find pins by board ID
    List<Pin> findByBoardId(Long boardId);
    
    // Find pins by board ID with pagination
    Page<Pin> findByBoardId(Long boardId, Pageable pageable);
    
    // Find public pins by user ID
    List<Pin> findByUserIdAndIsPrivate(Long userId, boolean isPrivate);
    
    // Find pins by title containing keyword
    List<Pin> findByTitleContainingIgnoreCase(String keyword);
    
    // Find pins by keywords containing search term
    List<Pin> findByKeywordsContainingIgnoreCase(String searchTerm);
    
    // Search pins by title or description or keywords
    @Query("SELECT p FROM Pin p WHERE p.isPrivate = false AND (LOWER(p.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
            + "LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
            + "LOWER(p.keywords) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Pin> searchPins(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    // Find pins by user ID and draft status
    List<Pin> findByUserIdAndIsDraft(Long userId, boolean isDraft);
    
    // Count pins by board ID
    long countByBoardId(Long boardId);
}