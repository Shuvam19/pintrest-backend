package com.pinterest.businessservice.controller;

import com.pinterest.businessservice.dto.ShowcaseDto;
import com.pinterest.businessservice.model.ApiResponse;
import com.pinterest.businessservice.service.ShowcaseService;
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
@RequestMapping("/api/showcases")
@RequiredArgsConstructor
public class ShowcaseController {

    private final ShowcaseService showcaseService;

    @PostMapping
    public ResponseEntity<ApiResponse<ShowcaseDto>> createShowcase(@Valid @RequestBody ShowcaseDto showcaseDto) {
        ShowcaseDto createdShowcase = showcaseService.createShowcase(showcaseDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Showcase created successfully", createdShowcase));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ShowcaseDto>> getShowcaseById(@PathVariable Long id) {
        ShowcaseDto showcase = showcaseService.getShowcaseById(id);
        return ResponseEntity.ok(ApiResponse.success("Showcase retrieved successfully", showcase));
    }

    @GetMapping("/business/{businessProfileId}")
    public ResponseEntity<ApiResponse<List<ShowcaseDto>>> getShowcasesByBusinessProfileId(
            @PathVariable Long businessProfileId) {
        
        List<ShowcaseDto> showcases = showcaseService.getShowcasesByBusinessProfileId(businessProfileId);
        return ResponseEntity.ok(ApiResponse.success("Showcases retrieved successfully", showcases));
    }

    @GetMapping("/business/{businessProfileId}/paged")
    public ResponseEntity<ApiResponse<Page<ShowcaseDto>>> getPagedShowcasesByBusinessProfileId(
            @PathVariable Long businessProfileId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "displayOrder") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<ShowcaseDto> showcasesPage = showcaseService.getShowcasesByBusinessProfileId(businessProfileId, pageable);
        return ResponseEntity.ok(ApiResponse.success("Showcases retrieved successfully", showcasesPage));
    }

    @GetMapping("/business/{businessProfileId}/featured")
    public ResponseEntity<ApiResponse<List<ShowcaseDto>>> getFeaturedShowcasesByBusinessProfileId(
            @PathVariable Long businessProfileId) {
        
        List<ShowcaseDto> featuredShowcases = showcaseService.getFeaturedShowcasesByBusinessProfileId(businessProfileId);
        return ResponseEntity.ok(ApiResponse.success("Featured showcases retrieved successfully", featuredShowcases));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<ShowcaseDto>>> searchShowcases(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ShowcaseDto> searchResults = showcaseService.searchShowcases(keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success("Search results retrieved successfully", searchResults));
    }

    @GetMapping("/theme/{theme}")
    public ResponseEntity<ApiResponse<Page<ShowcaseDto>>> getShowcasesByTheme(
            @PathVariable String theme,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ShowcaseDto> showcasesByTheme = showcaseService.getShowcasesByTheme(theme, pageable);
        return ResponseEntity.ok(ApiResponse.success("Showcases by theme retrieved successfully", showcasesByTheme));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ShowcaseDto>> updateShowcase(
            @PathVariable Long id,
            @Valid @RequestBody ShowcaseDto showcaseDto) {
        
        ShowcaseDto updatedShowcase = showcaseService.updateShowcase(id, showcaseDto);
        return ResponseEntity.ok(ApiResponse.success("Showcase updated successfully", updatedShowcase));
    }

    @PatchMapping("/{id}/toggle-featured")
    public ResponseEntity<ApiResponse<ShowcaseDto>> toggleFeaturedStatus(@PathVariable Long id) {
        ShowcaseDto updatedShowcase = showcaseService.toggleFeaturedStatus(id);
        return ResponseEntity.ok(ApiResponse.success("Featured status toggled successfully", updatedShowcase));
    }

    @PatchMapping("/{id}/toggle-active")
    public ResponseEntity<ApiResponse<ShowcaseDto>> toggleActiveStatus(@PathVariable Long id) {
        ShowcaseDto updatedShowcase = showcaseService.toggleActiveStatus(id);
        return ResponseEntity.ok(ApiResponse.success("Active status toggled successfully", updatedShowcase));
    }

    @PatchMapping("/business/{businessProfileId}/order")
    public ResponseEntity<ApiResponse<Void>> updateShowcaseOrder(
            @PathVariable Long businessProfileId,
            @RequestBody List<Long> showcaseIds) {
        
        showcaseService.updateShowcaseOrder(businessProfileId, showcaseIds);
        return ResponseEntity.ok(ApiResponse.success("Showcase order updated successfully", null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteShowcase(@PathVariable Long id) {
        showcaseService.deleteShowcase(id);
        return ResponseEntity.ok(ApiResponse.success("Showcase deleted successfully", null));
    }
}