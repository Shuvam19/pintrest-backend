package com.pinterest.businessservice.service.impl;

import com.pinterest.businessservice.dto.ShowcaseDto;
import com.pinterest.businessservice.dto.ShowcaseItemDto;
import com.pinterest.businessservice.exception.ResourceNotFoundException;
import com.pinterest.businessservice.model.BusinessProfile;
import com.pinterest.businessservice.model.Showcase;
import com.pinterest.businessservice.model.ShowcaseItem;
import com.pinterest.businessservice.repository.BusinessProfileRepository;
import com.pinterest.businessservice.repository.ShowcaseItemRepository;
import com.pinterest.businessservice.repository.ShowcaseRepository;
import com.pinterest.businessservice.service.ShowcaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShowcaseServiceImpl implements ShowcaseService {

    private final ShowcaseRepository showcaseRepository;
    private final ShowcaseItemRepository showcaseItemRepository;
    private final BusinessProfileRepository businessProfileRepository;

    @Override
    @Transactional
    public ShowcaseDto createShowcase(ShowcaseDto showcaseDto) {
        // Verify business profile exists
        BusinessProfile businessProfile = businessProfileRepository.findById(showcaseDto.getBusinessProfileId())
                .orElseThrow(() -> new ResourceNotFoundException("Business profile not found with id: " + showcaseDto.getBusinessProfileId()));

        // Convert DTO to entity
        Showcase showcase = convertToEntity(showcaseDto);
        
        // Set created and updated timestamps
        LocalDateTime now = LocalDateTime.now();
        showcase.setCreatedAt(now);
        showcase.setUpdatedAt(now);
        
        // Set default display order if not provided
        if (showcase.getDisplayOrder() == null) {
            Long maxOrder = showcaseRepository.findMaxDisplayOrderByBusinessProfileId(showcase.getBusinessProfileId());
            showcase.setDisplayOrder(maxOrder != null ? maxOrder + 1 : 1);
        }
        
        // Save showcase
        Showcase savedShowcase = showcaseRepository.save(showcase);
        
        // Process showcase items if provided
        List<ShowcaseItem> showcaseItems = new ArrayList<>();
        if (showcaseDto.getItems() != null && !showcaseDto.getItems().isEmpty()) {
            int order = 1;
            for (ShowcaseItemDto itemDto : showcaseDto.getItems()) {
                ShowcaseItem item = new ShowcaseItem();
                item.setShowcaseId(savedShowcase.getId());
                item.setPinId(itemDto.getPinId());
                item.setDescription(itemDto.getDescription());
                item.setDisplayOrder(itemDto.getDisplayOrder() != null ? itemDto.getDisplayOrder() : order++);
                item.setFeatured(itemDto.getFeatured() != null ? itemDto.getFeatured() : false);
                item.setActive(itemDto.getActive() != null ? itemDto.getActive() : true);
                item.setCreatedAt(now);
                item.setUpdatedAt(now);
                showcaseItems.add(item);
            }
            showcaseItemRepository.saveAll(showcaseItems);
        }
        
        // Convert back to DTO with additional info
        ShowcaseDto resultDto = convertToDto(savedShowcase);
        resultDto.setBusinessName(businessProfile.getBusinessName());
        resultDto.setBusinessLogoUrl(businessProfile.getLogoUrl());
        resultDto.setItemsCount(showcaseItems.size());
        resultDto.setItems(showcaseItems.stream().map(this::convertItemToDto).collect(Collectors.toList()));
        
        return resultDto;
    }

    @Override
    public ShowcaseDto getShowcaseById(Long id) {
        Showcase showcase = showcaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Showcase not found with id: " + id));
        
        ShowcaseDto showcaseDto = convertToDto(showcase);
        
        // Get business profile info
        BusinessProfile businessProfile = businessProfileRepository.findById(showcase.getBusinessProfileId())
                .orElseThrow(() -> new ResourceNotFoundException("Business profile not found with id: " + showcase.getBusinessProfileId()));
        showcaseDto.setBusinessName(businessProfile.getBusinessName());
        showcaseDto.setBusinessLogoUrl(businessProfile.getLogoUrl());
        
        // Get showcase items
        List<ShowcaseItem> items = showcaseItemRepository.findByShowcaseIdAndActiveOrderByDisplayOrderAsc(id, true);
        showcaseDto.setItemsCount(items.size());
        showcaseDto.setItems(items.stream().map(this::convertItemToDto).collect(Collectors.toList()));
        
        return showcaseDto;
    }

    @Override
    public List<ShowcaseDto> getShowcasesByBusinessProfileId(Long businessProfileId) {
        // Verify business profile exists
        if (!businessProfileRepository.existsById(businessProfileId)) {
            throw new ResourceNotFoundException("Business profile not found with id: " + businessProfileId);
        }
        
        List<Showcase> showcases = showcaseRepository.findByBusinessProfileIdOrderByDisplayOrderAsc(businessProfileId);
        return showcases.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Override
    public Page<ShowcaseDto> getShowcasesByBusinessProfileId(Long businessProfileId, Pageable pageable) {
        // Verify business profile exists
        if (!businessProfileRepository.existsById(businessProfileId)) {
            throw new ResourceNotFoundException("Business profile not found with id: " + businessProfileId);
        }
        
        Page<Showcase> showcasePage = showcaseRepository.findByBusinessProfileId(businessProfileId, pageable);
        return showcasePage.map(this::convertToDto);
    }

    @Override
    public List<ShowcaseDto> getFeaturedShowcasesByBusinessProfileId(Long businessProfileId) {
        // Verify business profile exists
        if (!businessProfileRepository.existsById(businessProfileId)) {
            throw new ResourceNotFoundException("Business profile not found with id: " + businessProfileId);
        }
        
        List<Showcase> showcases = showcaseRepository.findByBusinessProfileIdAndFeaturedTrueAndActiveTrueOrderByDisplayOrderAsc(businessProfileId);
        return showcases.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ShowcaseDto updateShowcase(Long id, ShowcaseDto showcaseDto) {
        Showcase showcase = showcaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Showcase not found with id: " + id));
        
        // Update fields
        showcase.setTitle(showcaseDto.getTitle());
        showcase.setDescription(showcaseDto.getDescription());
        showcase.setCoverImageUrl(showcaseDto.getCoverImageUrl());
        showcase.setTheme(showcaseDto.getTheme());
        
        if (showcaseDto.getFeatured() != null) {
            showcase.setFeatured(showcaseDto.getFeatured());
        }
        
        if (showcaseDto.getActive() != null) {
            showcase.setActive(showcaseDto.getActive());
        }
        
        if (showcaseDto.getDisplayOrder() != null) {
            showcase.setDisplayOrder(showcaseDto.getDisplayOrder());
        }
        
        showcase.setUpdatedAt(LocalDateTime.now());
        
        Showcase updatedShowcase = showcaseRepository.save(showcase);
        
        // Update showcase items if provided
        if (showcaseDto.getItems() != null && !showcaseDto.getItems().isEmpty()) {
            // Delete existing items
            showcaseItemRepository.deleteByShowcaseId(id);
            
            // Add new items
            List<ShowcaseItem> showcaseItems = new ArrayList<>();
            int order = 1;
            LocalDateTime now = LocalDateTime.now();
            
            for (ShowcaseItemDto itemDto : showcaseDto.getItems()) {
                ShowcaseItem item = new ShowcaseItem();
                item.setShowcaseId(id);
                item.setPinId(itemDto.getPinId());
                item.setDescription(itemDto.getDescription());
                item.setDisplayOrder(itemDto.getDisplayOrder() != null ? itemDto.getDisplayOrder() : order++);
                item.setFeatured(itemDto.getFeatured() != null ? itemDto.getFeatured() : false);
                item.setActive(itemDto.getActive() != null ? itemDto.getActive() : true);
                item.setCreatedAt(now);
                item.setUpdatedAt(now);
                showcaseItems.add(item);
            }
            
            showcaseItemRepository.saveAll(showcaseItems);
        }
        
        return getShowcaseById(id); // Return full DTO with items
    }

    @Override
    @Transactional
    public void deleteShowcase(Long id) {
        if (!showcaseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Showcase not found with id: " + id);
        }
        
        // Delete showcase items first
        showcaseItemRepository.deleteByShowcaseId(id);
        
        // Delete showcase
        showcaseRepository.deleteById(id);
    }

    @Override
    public Page<ShowcaseDto> searchShowcases(String keyword, Pageable pageable) {
        Page<Showcase> showcasePage = showcaseRepository.findByTitleContainingOrDescriptionContaining(keyword, keyword, pageable);
        return showcasePage.map(this::convertToDto);
    }

    @Override
    public Page<ShowcaseDto> getShowcasesByTheme(String theme, Pageable pageable) {
        Page<Showcase> showcasePage = showcaseRepository.findByTheme(theme, pageable);
        return showcasePage.map(this::convertToDto);
    }

    @Override
    @Transactional
    public ShowcaseDto toggleFeaturedStatus(Long id) {
        Showcase showcase = showcaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Showcase not found with id: " + id));
        
        showcase.setFeatured(!showcase.getFeatured());
        showcase.setUpdatedAt(LocalDateTime.now());
        
        Showcase updatedShowcase = showcaseRepository.save(showcase);
        return convertToDto(updatedShowcase);
    }

    @Override
    @Transactional
    public ShowcaseDto toggleActiveStatus(Long id) {
        Showcase showcase = showcaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Showcase not found with id: " + id));
        
        showcase.setActive(!showcase.getActive());
        showcase.setUpdatedAt(LocalDateTime.now());
        
        Showcase updatedShowcase = showcaseRepository.save(showcase);
        return convertToDto(updatedShowcase);
    }

    @Override
    @Transactional
    public void updateShowcaseOrder(Long businessProfileId, List<Long> showcaseIds) {
        // Verify business profile exists
        if (!businessProfileRepository.existsById(businessProfileId)) {
            throw new ResourceNotFoundException("Business profile not found with id: " + businessProfileId);
        }
        
        // Verify all showcases exist and belong to the business profile
        List<Showcase> showcases = showcaseRepository.findAllById(showcaseIds);
        
        if (showcases.size() != showcaseIds.size()) {
            throw new ResourceNotFoundException("One or more showcases not found");
        }
        
        for (Showcase showcase : showcases) {
            if (!showcase.getBusinessProfileId().equals(businessProfileId)) {
                throw new IllegalArgumentException("Showcase with id " + showcase.getId() + " does not belong to business profile " + businessProfileId);
            }
        }
        
        // Update display order
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < showcaseIds.size(); i++) {
            Long showcaseId = showcaseIds.get(i);
            Showcase showcase = showcases.stream()
                    .filter(s -> s.getId().equals(showcaseId))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Showcase not found with id: " + showcaseId));
            
            showcase.setDisplayOrder((long) (i + 1));
            showcase.setUpdatedAt(now);
        }
        
        showcaseRepository.saveAll(showcases);
    }

    // Helper methods for entity-DTO conversion
    private Showcase convertToEntity(ShowcaseDto dto) {
        Showcase entity = new Showcase();
        entity.setId(dto.getId());
        entity.setBusinessProfileId(dto.getBusinessProfileId());
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setCoverImageUrl(dto.getCoverImageUrl());
        entity.setTheme(dto.getTheme());
        entity.setFeatured(dto.getFeatured() != null ? dto.getFeatured() : false);
        entity.setActive(dto.getActive() != null ? dto.getActive() : true);
        entity.setDisplayOrder(dto.getDisplayOrder());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());
        return entity;
    }

    private ShowcaseDto convertToDto(Showcase entity) {
        ShowcaseDto dto = new ShowcaseDto();
        dto.setId(entity.getId());
        dto.setBusinessProfileId(entity.getBusinessProfileId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setCoverImageUrl(entity.getCoverImageUrl());
        dto.setTheme(entity.getTheme());
        dto.setFeatured(entity.getFeatured());
        dto.setActive(entity.getActive());
        dto.setDisplayOrder(entity.getDisplayOrder());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    private ShowcaseItemDto convertItemToDto(ShowcaseItem entity) {
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