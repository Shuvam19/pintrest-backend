package com.pinterest.businessservice.controller;

import com.pinterest.businessservice.dto.ShowcaseItemDto;
import com.pinterest.businessservice.model.ApiResponse;
import com.pinterest.businessservice.service.ShowcaseItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/showcase-items")
@RequiredArgsConstructor
public class ShowcaseItemController {

    private final ShowcaseItemService showcaseItemService;

    @PostMapping
    public ResponseEntity<ApiResponse<ShowcaseItemDto>> createShowcaseItem(@Valid @RequestBody ShowcaseItemDto showcaseItemDto) {
        ShowcaseItemDto createdItem = showcaseItemService.createShowcaseItem(showcaseItemDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Showcase item created successfully", createdItem));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ShowcaseItemDto>> getShowcaseItemById(@PathVariable Long id) {
        ShowcaseItemDto showcaseItem = showcaseItemService.getShowcaseItemById(id);
        return ResponseEntity.ok(ApiResponse.success("Showcase item retrieved successfully", showcaseItem));
    }

    @GetMapping("/showcase/{showcaseId}")
    public ResponseEntity<ApiResponse<List<ShowcaseItemDto>>> getShowcaseItemsByShowcaseId(
            @PathVariable Long showcaseId) {
        
        List<ShowcaseItemDto> showcaseItems = showcaseItemService.getShowcaseItemsByShowcaseId(showcaseId);
        return ResponseEntity.ok(ApiResponse.success("Showcase items retrieved successfully", showcaseItems));
    }

    @GetMapping("/showcase/{showcaseId}/paged")
    public ResponseEntity<ApiResponse<Page<ShowcaseItemDto>>> getPagedShowcaseItemsByShowcaseId(
            @PathVariable Long showcaseId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "displayOrder") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<ShowcaseItemDto> showcaseItemsPage = showcaseItemService.getShowcaseItemsByShowcaseId(showcaseId, pageable);
        return ResponseEntity.ok(ApiResponse.success("Showcase items retrieved successfully", showcaseItemsPage));
    }

    @GetMapping("/showcase/{showcaseId}/featured")
    public ResponseEntity<ApiResponse<List<ShowcaseItemDto>>> getFeaturedShowcaseItemsByShowcaseId(
            @PathVariable Long showcaseId) {
        
        List<ShowcaseItemDto> featuredItems = showcaseItemService.getFeaturedShowcaseItemsByShowcaseId(showcaseId);
        return ResponseEntity.ok(ApiResponse.success("Featured showcase items retrieved successfully", featuredItems));
    }

    @GetMapping("/pin/{pinId}")
    public ResponseEntity<ApiResponse<List<ShowcaseItemDto>>> getShowcaseItemsByPinId(@PathVariable Long pinId) {
        List<ShowcaseItemDto> showcaseItems = showcaseItemService.getShowcaseItemsByPinId(pinId);
        return ResponseEntity.ok(ApiResponse.success("Showcase items retrieved successfully", showcaseItems));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ShowcaseItemDto>> updateShowcaseItem(
            @PathVariable Long id,
            @Valid @RequestBody ShowcaseItemDto showcaseItemDto) {
        
        ShowcaseItemDto updatedItem = showcaseItemService.updateShowcaseItem(id, showcaseItemDto);
        return ResponseEntity.ok(ApiResponse.success("Showcase item updated successfully", updatedItem));
    }

    @PatchMapping("/{id}/toggle-featured")
    public ResponseEntity<ApiResponse<ShowcaseItemDto>> toggleFeaturedStatus(@PathVariable Long id) {
        ShowcaseItemDto updatedItem = showcaseItemService.toggleFeaturedStatus(id);
        return ResponseEntity.ok(ApiResponse.success("Featured status toggled successfully", updatedItem));
    }

    @PatchMapping("/{id}/toggle-active")
    public ResponseEntity<ApiResponse<ShowcaseItemDto>> toggleActiveStatus(@PathVariable Long id) {
        ShowcaseItemDto updatedItem = showcaseItemService.toggleActiveStatus(id);
        return ResponseEntity.ok(ApiResponse.success("Active status toggled successfully", updatedItem));
    }

    @PatchMapping("/showcase/{showcaseId}/order")
    public ResponseEntity<ApiResponse<Void>> updateShowcaseItemOrder(
            @PathVariable Long showcaseId,
            @RequestBody List<Long> showcaseItemIds) {
        
        showcaseItemService.updateShowcaseItemOrder(showcaseId, showcaseItemIds);
        return ResponseEntity.ok(ApiResponse.success("Showcase item order updated successfully", null));
    }

    @PostMapping("/showcase/{showcaseId}/pin/{pinId}")
    public ResponseEntity<ApiResponse<Void>> addPinToShowcase(
            @PathVariable Long showcaseId,
            @PathVariable Long pinId,
            @RequestParam(required = false) String description) {
        
        showcaseItemService.addPinToShowcase(showcaseId, pinId, description);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Pin added to showcase successfully", null));
    }

    @DeleteMapping("/showcase/{showcaseId}/pin/{pinId}")
    public ResponseEntity<ApiResponse<Void>> removePinFromShowcase(
            @PathVariable Long showcaseId,
            @PathVariable Long pinId) {
        
        showcaseItemService.removePinFromShowcase(showcaseId, pinId);
        return ResponseEntity.ok(ApiResponse.success("Pin removed from showcase successfully", null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteShowcaseItem(@PathVariable Long id) {
        showcaseItemService.deleteShowcaseItem(id);
        return ResponseEntity.ok(ApiResponse.success("Showcase item deleted successfully", null));
    }
}