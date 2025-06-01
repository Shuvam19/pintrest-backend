package com.pinterest.contentservice.service.impl;

import com.pinterest.contentservice.dto.KeywordDto;
import com.pinterest.contentservice.exception.ResourceNotFoundException;
import com.pinterest.contentservice.model.Keyword;
import com.pinterest.contentservice.model.Pin;
import com.pinterest.contentservice.repository.KeywordRepository;
import com.pinterest.contentservice.repository.PinRepository;
import com.pinterest.contentservice.service.KeywordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KeywordServiceImpl implements KeywordService {

    private final KeywordRepository keywordRepository;
    private final PinRepository pinRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    @Transactional
    public KeywordDto createKeyword(String name) {
        // Check if keyword already exists
        Optional<Keyword> existingKeyword = keywordRepository.findByNameIgnoreCase(name.trim());
        
        if (existingKeyword.isPresent()) {
            return mapToDto(existingKeyword.get());
        }
        
        // Create new keyword
        Keyword keyword = Keyword.builder()
                .name(name.trim())
                .build();
        
        Keyword savedKeyword = keywordRepository.save(keyword);
        return mapToDto(savedKeyword);
    }

    @Override
    public KeywordDto getKeywordById(Long id) {
        Keyword keyword = keywordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Keyword not found with id: " + id));
        
        return mapToDto(keyword);
    }

    @Override
    public KeywordDto getKeywordByName(String name) {
        Keyword keyword = keywordRepository.findByNameIgnoreCase(name.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Keyword not found with name: " + name));
        
        return mapToDto(keyword);
    }

    @Override
    public List<KeywordDto> getAllKeywords() {
        List<Keyword> keywords = keywordRepository.findAll();
        return keywords.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<KeywordDto> searchKeywords(String searchTerm) {
        List<Keyword> keywords = keywordRepository.findByNameContainingIgnoreCase(searchTerm.trim());
        return keywords.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<KeywordDto> getMostUsedKeywords() {
        List<Keyword> keywords = keywordRepository.findTop10ByOrderByPinsSizeDesc();
        return keywords.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Set<Keyword> processKeywords(List<String> keywordNames) {
        if (keywordNames == null || keywordNames.isEmpty()) {
            return new HashSet<>();
        }
        
        // Clean and normalize keywords
        List<String> normalizedKeywords = keywordNames.stream()
                .map(String::trim)
                .filter(k -> !k.isEmpty())
                .map(String::toLowerCase)
                .distinct()
                .collect(Collectors.toList());
        
        // Find existing keywords
        List<Keyword> existingKeywords = keywordRepository.findByNameInIgnoreCase(normalizedKeywords);
        Map<String, Keyword> keywordMap = existingKeywords.stream()
                .collect(Collectors.toMap(k -> k.getName().toLowerCase(), k -> k));
        
        Set<Keyword> result = new HashSet<>(existingKeywords);
        
        // Create new keywords for those that don't exist
        for (String name : normalizedKeywords) {
            if (!keywordMap.containsKey(name)) {
                Keyword newKeyword = Keyword.builder()
                        .name(name)
                        .build();
                
                Keyword savedKeyword = keywordRepository.save(newKeyword);
                result.add(savedKeyword);
            }
        }
        
        return result;
    }

    @Override
    public List<KeywordDto> getKeywordsForPin(Long pinId) {
        Pin pin = pinRepository.findById(pinId)
                .orElseThrow(() -> new ResourceNotFoundException("Pin not found with id: " + pinId));
        
        return pin.getKeywords().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> parseKeywordString(String keywordsString) {
        if (keywordsString == null || keywordsString.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        return Arrays.stream(keywordsString.split(","))
                .map(String::trim)
                .filter(k -> !k.isEmpty())
                .collect(Collectors.toList());
    }

    @Override
    public String formatKeywordsToString(Set<Keyword> keywords) {
        if (keywords == null || keywords.isEmpty()) {
            return "";
        }
        
        return keywords.stream()
                .map(Keyword::getName)
                .collect(Collectors.joining(", "));
    }
    
    private KeywordDto mapToDto(Keyword keyword) {
        return KeywordDto.builder()
                .id(keyword.getId())
                .name(keyword.getName())
                .pinCount(keyword.getPins().size())
                .createdAt(keyword.getCreatedAt() != null ? keyword.getCreatedAt().format(DATE_FORMATTER) : null)
                .updatedAt(keyword.getUpdatedAt() != null ? keyword.getUpdatedAt().format(DATE_FORMATTER) : null)
                .build();
    }
}