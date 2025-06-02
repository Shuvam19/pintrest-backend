package com.pinterest.businessservice.service;

import com.pinterest.businessservice.dto.ShowcaseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ShowcaseService {
    
    ShowcaseDto createShowcase(ShowcaseDto showcaseDto);
    
    ShowcaseDto getShowcaseById(Long id);
    
    List<ShowcaseDto> getShowcasesByBusinessProfileId(Long businessProfileId);
    
    Page<ShowcaseDto> getShowcasesByBusinessProfileId(Long businessProfileId, Pageable pageable);
    
    List<ShowcaseDto> getFeaturedShowcasesByBusinessProfileId(Long businessProfileId);
    
    ShowcaseDto updateShowcase(Long id, ShowcaseDto showcaseDto);
    
    void deleteShowcase(Long id);
    
    Page<ShowcaseDto> searchShowcases(String keyword, Pageable pageable);
    
    Page<ShowcaseDto> getShowcasesByTheme(String theme, Pageable pageable);
    
    ShowcaseDto toggleFeaturedStatus(Long id);
    
    ShowcaseDto toggleActiveStatus(Long id);
    
    void updateShowcaseOrder(Long businessProfileId, List<Long> showcaseIds);
}