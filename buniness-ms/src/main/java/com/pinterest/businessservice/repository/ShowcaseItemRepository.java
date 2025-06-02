package com.pinterest.businessservice.repository;

import com.pinterest.businessservice.model.ShowcaseItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShowcaseItemRepository extends JpaRepository<ShowcaseItem, Long> {
    
    List<ShowcaseItem> findByShowcaseId(Long showcaseId);
    
    List<ShowcaseItem> findByShowcaseIdAndActive(Long showcaseId, boolean active);
    
    List<ShowcaseItem> findByPinId(Long pinId);
    
    @Query("SELECT si FROM ShowcaseItem si WHERE si.showcaseId = :showcaseId ORDER BY si.displayOrder ASC")
    List<ShowcaseItem> findByShowcaseIdOrderByDisplayOrder(Long showcaseId);
    
    @Query("SELECT si FROM ShowcaseItem si WHERE si.showcaseId = :showcaseId AND si.featured = true")
    List<ShowcaseItem> findFeaturedItemsByShowcaseId(Long showcaseId);
    
    void deleteByShowcaseId(Long showcaseId);
}