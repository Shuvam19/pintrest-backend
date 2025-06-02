package com.pinterest.businessservice.repository;

import com.pinterest.businessservice.model.Showcase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShowcaseRepository extends JpaRepository<Showcase, Long> {
    
    List<Showcase> findByBusinessProfileId(Long businessProfileId);
    
    List<Showcase> findByBusinessProfileIdAndActive(Long businessProfileId, boolean active);
    
    List<Showcase> findByBusinessProfileIdAndFeatured(Long businessProfileId, boolean featured);
    
    @Query("SELECT s FROM Showcase s WHERE s.businessProfileId = :businessProfileId ORDER BY s.displayOrder ASC")
    List<Showcase> findByBusinessProfileIdOrderByDisplayOrder(Long businessProfileId);
    
    @Query("SELECT s FROM Showcase s WHERE LOWER(s.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(s.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Showcase> searchByTitleOrDescription(String keyword);
    
    @Query("SELECT s FROM Showcase s WHERE s.theme = :theme AND s.active = true")
    List<Showcase> findByTheme(String theme);
}