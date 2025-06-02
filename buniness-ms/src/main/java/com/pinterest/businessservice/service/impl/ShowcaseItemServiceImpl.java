package com.pinterest.businessservice.service.impl;

import com.pinterest.businessservice.dto.ShowcaseItemDto;
import com.pinterest.businessservice.exception.ResourceNotFoundException;
import com.pinterest.businessservice.model.ShowcaseItem;
import com.pinterest.businessservice.repository.ShowcaseItemRepository;
import com.pinterest.businessservice.repository.ShowcaseRepository;
import com.pinterest.businessservice.service.ShowcaseItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShowcaseItemServiceImpl implements ShowcaseItemService {

    private final ShowcaseItemRepository showcaseItemRepository;
    private final ShowcaseRepository showcaseRepository;

    @Override
    @Transactional
    public ShowcaseItemDto createShowcaseItem(ShowcaseItemDto showcaseItemDto) {
        // Verify showcase exists
        if (!showcaseRepository.existsById(showcaseItemDto.getShowcaseId())) {
            throw new ResourceNotFoundException("Showcase not found with id: " + showcaseItemDto.getShowcaseId());
        }
        
        // Convert DTO to entity
        ShowcaseItem showcaseItem = convertToEntity(showcaseItemDto);
        
        // Set created and updated timestamps
        LocalDateTime now = LocalDateTime.now();
        showcaseItem.setCreatedAt(now);
        showcaseItem.setUpdatedAt(now);
        
        // Set default display order if not provided
        if (showcaseItem.getDisplayOrder() == null) {
            Long maxOrder = showcaseItemRepository.findMaxDisplayOrderByShowcaseId(showcaseItem.getShowcaseId());
            showcaseItem.setDisplayOrder(maxOrder != null ? maxOrder + 1 : 1);
        }
        
        // Save showcase item
        ShowcaseItem savedItem = showcaseItemRepository.save(showcaseItem);
        
        return convertToDto(savedItem);
    }

    @Override
    public ShowcaseItemDto getShowcaseItemById(Long id) {
        ShowcaseItem showcaseItem = showcaseItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Showcase item not found with id: " + id));
        
        return convertToDto(showcaseItem);
    }

    @Override
    public List<ShowcaseItemDto> getShowcaseItemsByShowcaseId(Long showcaseId) {
        // Verify showcase exists
        if (!showcaseRepository.existsById(showcaseId)) {
            throw new ResourceNotFoundException("Showcase not found with id: " + showcaseId);
        }
        
        List<ShowcaseItem> items = showcaseItemRepository.findByShowcaseIdOrderByDisplayOrderAsc(showcaseId);
        return items.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Override
    public Page<ShowcaseItemDto> getShowcaseItemsByShowcaseId(Long showcaseId, Pageable pageable) {
        // Verify showcase exists
        if (!showcaseRepository.existsById(showcaseId)) {
            throw new ResourceNotFoundException("Showcase not found with id: " + showcaseId);
        }
        
        Page<ShowcaseItem> itemsPage = showcaseItemRepository.findByShowcaseId(showcaseId, pageable);
        return itemsPage.map(this::convertToDto);
    }

    @Override
    public List<ShowcaseItemDto> getFeaturedShowcaseItemsByShowcaseId(Long showcaseId) {
        // Verify showcase exists
        if (!showcaseRepository.existsById(showcaseId)) {
            throw new ResourceNotFoundException("Showcase not found with id: " + showcaseId);
        }
        
        List<ShowcaseItem> items = showcaseItemRepository.findByShowcaseIdAndFeaturedTrueAndActiveTrueOrderByDisplayOrderAsc(showcaseId);
        return items.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ShowcaseItemDto updateShowcaseItem(Long id, ShowcaseItemDto showcaseItemDto) {
        ShowcaseItem showcaseItem = showcaseItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Showcase item not found with id: " + id));
        
        // Update fields
        showcaseItem.setDescription(showcaseItemDto.getDescription());
        
        if (showcaseItemDto.getFeatured() != null) {
            showcaseItem.setFeatured(showcaseItemDto.getFeatured());
        }
        
        if (showcaseItemDto.getActive() != null) {
            showcaseItem.setActive(showcaseItemDto.getActive());
        }
        
        if (showcaseItemDto.getDisplayOrder() != null) {
            showcaseItem.setDisplayOrder(showcaseItemDto.getDisplayOrder());
        }
        
        showcaseItem.setUpdatedAt(LocalDateTime.now());
        
        ShowcaseItem updatedItem = showcaseItemRepository.save(showcaseItem);
        return convertToDto(updatedItem);
    }

    @Override
    @Transactional
    public void deleteShowcaseItem(Long id) {
        if (!showcaseItemRepository.existsById(id)) {
            throw new ResourceNotFoundException("Showcase item not found with id: " + id);
        }
        
        showcaseItemRepository.deleteById(id);
    }

    @Override
    public List<ShowcaseItemDto> getShowcaseItemsByPinId(Long pinId) {
        List<ShowcaseItem> items = showcaseItemRepository.findByPinId(pinId);
        return items.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ShowcaseItemDto toggleFeaturedStatus(Long id) {
        ShowcaseItem showcaseItem = showcaseItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Showcase item not found with id: " + id));
        
        showcaseItem.setFeatured(!showcaseItem.getFeatured());
        showcaseItem.setUpdatedAt(LocalDateTime.now());
        
        ShowcaseItem updatedItem = showcaseItemRepository.save(showcaseItem);
        return convertToDto(updatedItem);
    }

    @Override
    @Transactional
    public ShowcaseItemDto toggleActiveStatus(Long id) {
        ShowcaseItem showcaseItem = showcaseItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Showcase item not found with id: " + id));
        
        showcaseItem.setActive(!showcaseItem.getActive());
        showcaseItem.setUpdatedAt(LocalDateTime.now());
        
        ShowcaseItem updatedItem = showcaseItemRepository.save(showcaseItem);
        return convertToDto(updatedItem);
    }

    @Override
    @Transactional
    public void updateShowcaseItemOrder(Long showcaseId, List<Long> showcaseItemIds) {
        // Verify showcase exists
        if (!showcaseRepository.existsById(showcaseId)) {
            throw new ResourceNotFoundException("Showcase not found with id: " + showcaseId);
        }
        
        // Verify all showcase items exist and belong to the showcase
        List<ShowcaseItem> items = showcaseItemRepository.findAllById(showcaseItemIds);
        
        if (items.size() != showcaseItemIds.size()) {
            throw new ResourceNotFoundException("One or more showcase items not found");
        }
        
        for (ShowcaseItem item : items) {
            if (!item.getShowcaseId().equals(showcaseId)) {
                throw new IllegalArgumentException("Showcase item with id " + item.getId() + " does not belong to showcase " + showcaseId);
            }
        }
        
        // Update display order
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < showcaseItemIds.size(); i++) {
            Long itemId = showcaseItemIds.get(i);
            ShowcaseItem item = items.stream()
                    .filter(s -> s.getId().equals(itemId))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Showcase item not found with id: " + itemId));
            
            item.setDisplayOrder((long) (i + 1));
            item.setUpdatedAt(now);
        }
        
        showcaseItemRepository.saveAll(items);
    }

    @Override
    @Transactional
    public void addPinToShowcase(Long showcaseId, Long pinId, String description) {
        // Verify showcase exists
        if (!showcaseRepository.existsById(showcaseId)) {
            throw new ResourceNotFoundException("Showcase not found with id: " + showcaseId);
        }
        
        // Check if pin already exists in showcase
        if (showcaseItemRepository.existsByShowcaseIdAndPinId(showcaseId, pinId)) {
            throw new IllegalArgumentException("Pin already exists in this showcase");
        }
        
        // Create new showcase item
        ShowcaseItem item = new ShowcaseItem();
        item.setShowcaseId(showcaseId);
        item.setPinId(pinId);
        item.setDescription(description);
        
        // Set display order
        Long maxOrder = showcaseItemRepository.findMaxDisplayOrderByShowcaseId(showcaseId);
        item.setDisplayOrder(maxOrder != null ? maxOrder + 1 : 1);
        
        // Set default values
        item.setFeatured(false);
        item.setActive(true);
        
        // Set timestamps
        LocalDateTime now = LocalDateTime.now();
        item.setCreatedAt(now);
        item.setUpdatedAt(now);
        
        showcaseItemRepository.save(item);
    }

    @Override
    @Transactional
    public void removePinFromShowcase(Long showcaseId, Long pinId) {
        // Verify showcase exists
        if (!showcaseRepository.existsById(showcaseId)) {
            throw new ResourceNotFoundException("Showcase not found with id: " + showcaseId);
        }
        
        // Find showcase item
        ShowcaseItem item = showcaseItemRepository.findByShowcaseIdAndPinId(showcaseId, pinId)
                .orElseThrow(() -> new ResourceNotFoundException("Pin not found in this showcase"));
        
        // Delete showcase item
        showcaseItemRepository.delete(item);
    }

    // Helper methods for entity-DTO conversion
    private ShowcaseItem convertToEntity(ShowcaseItemDto dto) {
        ShowcaseItem entity = new ShowcaseItem();
        entity.setId(dto.getId());
        entity.setShowcaseId(dto.getShowcaseId());
        entity.setPinId(dto.getPinId());
        entity.setDescription(dto.getDescription());
        entity.setDisplayOrder(dto.getDisplayOrder());
        entity.setFeatured(dto.getFeatured() != null ? dto.getFeatured() : false);
        entity.setActive(dto.getActive() != null ? dto.getActive() : true);
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());
        return entity;
    }

    private ShowcaseItemDto convertToDto(ShowcaseItem entity) {
        ShowcaseItemDto dto = new ShowcaseItemDto();
        dto.setId(entity.getId());
        dto.setShowcaseId(entity.getShowcaseId());
        dto.setPinId(entity.getPinId());
        dto.setDescription(entity.getDescription());
        dto.setDisplayOrder(entity.getDisplayOrder());
        dto.setFeatured(entity.getFeatured());
        dto.setActive(entity.getActive());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        // Note: Pin details would need to be fetched from a Pin service
        return dto;
    }
}