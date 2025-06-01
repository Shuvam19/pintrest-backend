package com.pinterest.contentservice.service;

import com.pinterest.contentservice.dto.KeywordDto;
import com.pinterest.contentservice.model.Keyword;

import java.util.List;
import java.util.Set;

public interface KeywordService {
    
    // Create a new keyword
    KeywordDto createKeyword(String name);
    
    // Get a keyword by ID
    KeywordDto getKeywordById(Long id);
    
    // Get a keyword by name
    KeywordDto getKeywordByName(String name);
    
    // Get all keywords
    List<KeywordDto> getAllKeywords();
    
    // Search keywords by name
    List<KeywordDto> searchKeywords(String searchTerm);
    
    // Get most used keywords
    List<KeywordDto> getMostUsedKeywords();
    
    // Process a list of keyword strings and return Keyword entities
    Set<Keyword> processKeywords(List<String> keywordNames);
    
    // Get keywords for a pin
    List<KeywordDto> getKeywordsForPin(Long pinId);
    
    // Convert string of comma-separated keywords to a list
    List<String> parseKeywordString(String keywordsString);
    
    // Convert a list of keywords to a comma-separated string
    String formatKeywordsToString(Set<Keyword> keywords);
}