package com.pinterest.contentservice.controller;

import com.pinterest.contentservice.dto.ApiResponse;
import com.pinterest.contentservice.dto.KeywordDto;
import com.pinterest.contentservice.service.KeywordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/content/keywords")
@RequiredArgsConstructor
public class KeywordController {

    private final KeywordService keywordService;

    @PostMapping
    public ResponseEntity<ApiResponse<KeywordDto>> createKeyword(@RequestParam String name) {
        KeywordDto keywordDto = keywordService.createKeyword(name);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Keyword created successfully", keywordDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<KeywordDto>> getKeywordById(@PathVariable Long id) {
        KeywordDto keywordDto = keywordService.getKeywordById(id);
        return ResponseEntity.ok(ApiResponse.success("Keyword retrieved successfully", keywordDto));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<ApiResponse<KeywordDto>> getKeywordByName(@PathVariable String name) {
        KeywordDto keywordDto = keywordService.getKeywordByName(name);
        return ResponseEntity.ok(ApiResponse.success("Keyword retrieved successfully", keywordDto));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<KeywordDto>>> getAllKeywords() {
        List<KeywordDto> keywords = keywordService.getAllKeywords();
        return ResponseEntity.ok(ApiResponse.success("Keywords retrieved successfully", keywords));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<KeywordDto>>> searchKeywords(@RequestParam String query) {
        List<KeywordDto> keywords = keywordService.searchKeywords(query);
        return ResponseEntity.ok(ApiResponse.success("Keywords retrieved successfully", keywords));
    }

    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<List<KeywordDto>>> getMostUsedKeywords() {
        List<KeywordDto> keywords = keywordService.getMostUsedKeywords();
        return ResponseEntity.ok(ApiResponse.success("Popular keywords retrieved successfully", keywords));
    }

    @GetMapping("/pin/{pinId}")
    public ResponseEntity<ApiResponse<List<KeywordDto>>> getKeywordsForPin(@PathVariable Long pinId) {
        List<KeywordDto> keywords = keywordService.getKeywordsForPin(pinId);
        return ResponseEntity.ok(ApiResponse.success("Pin keywords retrieved successfully", keywords));
    }
}