package com.pinterest.businessservice.service;

import com.pinterest.businessservice.dto.ShowcaseItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ShowcaseItemService {
    
    ShowcaseItemDto createShowcaseItem(ShowcaseItemDto showcaseItemDto);
    
    ShowcaseItemDto getShowcaseItemById(Long id);
    
    List<ShowcaseItemDto> getShowcaseItemsByShowcaseId(Long showcaseId);
    
    Page<ShowcaseItemDto> getShowcaseItemsByShowcaseId(Long showcaseId, Pageable pageable);
    
    List<ShowcaseItemDto> getFeaturedShowcaseItemsByShowcaseId(Long showcaseId);
    
    ShowcaseItemDto updateShowcaseItem(Long id, ShowcaseItemDto showcaseItemDto);
    
    void deleteShowcaseItem(Long id);
    
    List<ShowcaseItemDto> getShowcaseItemsByPinId(Long pinId);
    
    ShowcaseItemDto toggleFeaturedStatus(Long id);
    
    ShowcaseItemDto toggleActiveStatus(Long id);
    
    void updateShowcaseItemOrder(Long showcaseId, List<Long> showcaseItemIds);
    
    void addPinToShowcase(Long showcaseId, Long pinId, String description);
    
    void removePinFromShowcase(Long showcaseId, Long pinId);
}