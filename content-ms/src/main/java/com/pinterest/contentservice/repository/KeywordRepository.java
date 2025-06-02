package com.pinterest.contentservice.repository;

import com.pinterest.contentservice.model.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KeywordRepository extends JpaRepository<Keyword, Long> {
    
    // Find keyword by name (case insensitive)
    Optional<Keyword> findByNameIgnoreCase(String name);
    
    // Find keywords containing the search term
    List<Keyword> findByNameContainingIgnoreCase(String searchTerm);
    
    // Find keywords by multiple names
    List<Keyword> findByNameInIgnoreCase(List<String> names);
    
    // Find most used keywords (by pin count)
    List<Keyword> findTop10ByOrderByPinsSizeDesc();
}