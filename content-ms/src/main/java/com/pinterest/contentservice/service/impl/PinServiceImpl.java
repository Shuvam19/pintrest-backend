package com.pinterest.contentservice.service.impl;

import com.pinterest.contentservice.dto.KeywordDto;
import com.pinterest.contentservice.dto.PinDto;
import com.pinterest.contentservice.dto.PinRequest;
import com.pinterest.contentservice.exception.ResourceNotFoundException;
import com.pinterest.contentservice.model.Board;
import com.pinterest.contentservice.model.Keyword;
import com.pinterest.contentservice.model.Pin;
import com.pinterest.contentservice.repository.BoardRepository;
import com.pinterest.contentservice.repository.PinRepository;
import com.pinterest.contentservice.service.KeywordService;
import com.pinterest.contentservice.service.PinService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PinServiceImpl implements PinService {

    private final PinRepository pinRepository;
    private final BoardRepository boardRepository;
    private final KeywordService keywordService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    @Transactional
    public PinDto createPin(PinRequest pinRequest) {
        Pin pin = mapToEntity(pinRequest);
        
        // If board ID is provided, associate pin with the board
        if (pinRequest.getBoardId() != null) {
            Board board = boardRepository.findById(pinRequest.getBoardId())
                    .orElseThrow(() -> new ResourceNotFoundException("Board not found with id: " + pinRequest.getBoardId()));
            pin.setBoard(board);
        }
        
        // Process keywords
        if (pinRequest.getKeywords() != null && !pinRequest.getKeywords().isEmpty()) {
            List<String> keywordNames = keywordService.parseKeywordString(pinRequest.getKeywords());
            Set<Keyword> keywords = keywordService.processKeywords(keywordNames);
            pin.setKeywords(keywords);
            
            // Set the keywordsText field for backward compatibility
            pin.setKeywordsText(pinRequest.getKeywords());
        }
        
        Pin savedPin = pinRepository.save(pin);
        return mapToDto(savedPin);
    }

    @Override
    public PinDto getPinById(Long pinId) {
        Pin pin = pinRepository.findById(pinId)
                .orElseThrow(() -> new ResourceNotFoundException("Pin not found with id: " + pinId));
        return mapToDto(pin);
    }

    @Override
    @Transactional
    public PinDto updatePin(Long pinId, PinRequest pinRequest) {
        Pin pin = pinRepository.findById(pinId)
                .orElseThrow(() -> new ResourceNotFoundException("Pin not found with id: " + pinId));
        
        // Update pin properties
        pin.setTitle(pinRequest.getTitle());
        pin.setDescription(pinRequest.getDescription());
        pin.setImageUrl(pinRequest.getImageUrl());
        pin.setVideoUrl(pinRequest.getVideoUrl());
        pin.setSourceUrl(pinRequest.getSourceUrl());
        pin.setAttribution(pinRequest.getAttribution());
        pin.setPrivate(pinRequest.isPrivate());
        pin.setDraft(pinRequest.isDraft());
        
        // Process keywords
        if (pinRequest.getKeywords() != null) {
            List<String> keywordNames = keywordService.parseKeywordString(pinRequest.getKeywords());
            Set<Keyword> keywords = keywordService.processKeywords(keywordNames);
            pin.setKeywords(keywords);
            
            // Set the keywordsText field for backward compatibility
            pin.setKeywordsText(pinRequest.getKeywords());
        }
        
        // Update board if changed
        if (pinRequest.getBoardId() != null && 
                (pin.getBoard() == null || !pin.getBoard().getId().equals(pinRequest.getBoardId()))) {
            Board board = boardRepository.findById(pinRequest.getBoardId())
                    .orElseThrow(() -> new ResourceNotFoundException("Board not found with id: " + pinRequest.getBoardId()));
            pin.setBoard(board);
        }
        
        Pin updatedPin = pinRepository.save(pin);
        return mapToDto(updatedPin);
    }

    @Override
    @Transactional
    public void deletePin(Long pinId) {
        Pin pin = pinRepository.findById(pinId)
                .orElseThrow(() -> new ResourceNotFoundException("Pin not found with id: " + pinId));
        pinRepository.delete(pin);
    }

    @Override
    public List<PinDto> getPinsByUserId(Long userId) {
        List<Pin> pins = pinRepository.findByUserId(userId);
        return pins.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public Page<PinDto> getPinsByUserId(Long userId, Pageable pageable) {
        Page<Pin> pinPage = pinRepository.findByUserId(userId, pageable);
        return pinPage.map(this::mapToDto);
    }

    @Override
    public List<PinDto> getPinsByBoardId(Long boardId) {
        List<Pin> pins = pinRepository.findByBoardId(boardId);
        return pins.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public Page<PinDto> getPinsByBoardId(Long boardId, Pageable pageable) {
        Page<Pin> pinPage = pinRepository.findByBoardId(boardId, pageable);
        return pinPage.map(this::mapToDto);
    }

    @Override
    public Page<PinDto> searchPins(String searchTerm, Pageable pageable) {
        Page<Pin> pinPage = pinRepository.searchPins(searchTerm, pageable);
        return pinPage.map(this::mapToDto);
    }

    @Override
    public List<PinDto> getDraftPinsByUserId(Long userId) {
        List<Pin> pins = pinRepository.findByUserIdAndIsDraft(userId, true);
        return pins.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PinDto publishDraftPin(Long pinId) {
        Pin pin = pinRepository.findById(pinId)
                .orElseThrow(() -> new ResourceNotFoundException("Pin not found with id: " + pinId));
        
        // Ensure the pin is a draft
        if (!pin.isDraft()) {
            throw new IllegalStateException("Pin is already published");
        }
        
        pin.setDraft(false);
        Pin publishedPin = pinRepository.save(pin);
        return mapToDto(publishedPin);
    }

    @Override
    @Transactional
    public PinDto savePinToBoard(Long pinId, Long boardId) {
        Pin pin = pinRepository.findById(pinId)
                .orElseThrow(() -> new ResourceNotFoundException("Pin not found with id: " + pinId));
        
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ResourceNotFoundException("Board not found with id: " + boardId));
        
        pin.setBoard(board);
        Pin savedPin = pinRepository.save(pin);
        return mapToDto(savedPin);
    }
    
    // Helper method to map Pin entity to PinDto
    private PinDto mapToDto(Pin pin) {
        PinDto pinDto = PinDto.builder()
                .id(pin.getId())
                .title(pin.getTitle())
                .description(pin.getDescription())
                .imageUrl(pin.getImageUrl())
                .videoUrl(pin.getVideoUrl())
                .sourceUrl(pin.getSourceUrl())
                .attribution(pin.getAttribution())
                .keywords(pin.getKeywordsText())
                .isPrivate(pin.isPrivate())
                .isDraft(pin.isDraft())
                .userId(pin.getUserId())
                .createdAt(pin.getCreatedAt() != null ? pin.getCreatedAt().format(DATE_FORMATTER) : null)
                .updatedAt(pin.getUpdatedAt() != null ? pin.getUpdatedAt().format(DATE_FORMATTER) : null)
                .build();
        
        // Set board information if available
        if (pin.getBoard() != null) {
            pinDto.setBoardId(pin.getBoard().getId());
            pinDto.setBoardTitle(pin.getBoard().getTitle());
        }
        
        // Set keyword list if available
        if (pin.getKeywords() != null && !pin.getKeywords().isEmpty()) {
            List<KeywordDto> keywordDtos = pin.getKeywords().stream()
                    .map(keyword -> KeywordDto.builder()
                            .id(keyword.getId())
                            .name(keyword.getName())
                            .pinCount(keyword.getPins().size())
                            .createdAt(keyword.getCreatedAt() != null ? keyword.getCreatedAt().format(DATE_FORMATTER) : null)
                            .updatedAt(keyword.getUpdatedAt() != null ? keyword.getUpdatedAt().format(DATE_FORMATTER) : null)
                            .build())
                    .collect(Collectors.toList());
            pinDto.setKeywordList(keywordDtos);
        }
        
        return pinDto;
    }
    
    // Helper method to map PinRequest to Pin entity
    private Pin mapToEntity(PinRequest pinRequest) {
        return Pin.builder()
                .title(pinRequest.getTitle())
                .description(pinRequest.getDescription())
                .imageUrl(pinRequest.getImageUrl())
                .videoUrl(pinRequest.getVideoUrl())
                .sourceUrl(pinRequest.getSourceUrl())
                .attribution(pinRequest.getAttribution())
                .keywordsText(pinRequest.getKeywords())
                .isPrivate(pinRequest.isPrivate())
                .isDraft(pinRequest.isDraft())
                .userId(pinRequest.getUserId())
                .build();
    }
}