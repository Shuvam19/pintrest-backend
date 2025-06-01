package com.pinterest.contentservice.service.impl;

import com.pinterest.contentservice.dto.BoardDto;
import com.pinterest.contentservice.dto.BoardRequest;
import com.pinterest.contentservice.dto.PinDto;
import com.pinterest.contentservice.exception.ResourceNotFoundException;
import com.pinterest.contentservice.model.Board;
import com.pinterest.contentservice.repository.BoardRepository;
import com.pinterest.contentservice.repository.PinRepository;
import com.pinterest.contentservice.service.BoardService;
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
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final PinRepository pinRepository;
    private final PinService pinService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    @Transactional
    public BoardDto createBoard(BoardRequest boardRequest) {
        Board board = mapToEntity(boardRequest);
        Board savedBoard = boardRepository.save(board);
        return mapToDto(savedBoard);
    }

    @Override
    public BoardDto getBoardById(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ResourceNotFoundException("Board not found with id: " + boardId));
        
        BoardDto boardDto = mapToDto(board);
        
        // Get pins for this board
        List<PinDto> pins = pinService.getPinsByBoardId(boardId);
        boardDto.setPins(pins);
        boardDto.setPinCount(pins.size());
        
        return boardDto;
    }

    @Override
    @Transactional
    public BoardDto updateBoard(Long boardId, BoardRequest boardRequest) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ResourceNotFoundException("Board not found with id: " + boardId));
        
        // Update board properties
        board.setTitle(boardRequest.getTitle());
        board.setDescription(boardRequest.getDescription());
        board.setCoverImageUrl(boardRequest.getCoverImageUrl());
        board.setPrivate(boardRequest.isPrivate());
        board.setCategory(boardRequest.getCategory());
        board.setDisplayOrder(boardRequest.getDisplayOrder());
        board.setCollaborative(boardRequest.isCollaborative());
        
        Board updatedBoard = boardRepository.save(board);
        return mapToDto(updatedBoard);
    }

    @Override
    @Transactional
    public void deleteBoard(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new ResourceNotFoundException("Board not found with id: " + boardId));
        boardRepository.delete(board);
    }

    @Override
    public List<BoardDto> getBoardsByUserId(Long userId) {
        List<Board> boards = boardRepository.findByUserId(userId);
        return boards.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public Page<BoardDto> getBoardsByUserId(Long userId, Pageable pageable) {
        Page<Board> boardPage = boardRepository.findByUserId(userId, pageable);
        return boardPage.map(board -> {
            BoardDto boardDto = mapToDto(board);
            long pinCount = pinRepository.countByBoardId(board.getId());
            boardDto.setPinCount((int) pinCount);
            return boardDto;
        });
    }

    @Override
    public Page<BoardDto> searchBoards(String searchTerm, Pageable pageable) {
        Page<Board> boardPage = boardRepository.searchBoards(searchTerm, pageable);
        return boardPage.map(board -> {
            BoardDto boardDto = mapToDto(board);
            long pinCount = pinRepository.countByBoardId(board.getId());
            boardDto.setPinCount((int) pinCount);
            return boardDto;
        });
    }

    @Override
    public List<BoardDto> getBoardsByCategory(String category) {
        List<Board> boards = boardRepository.findByCategory(category);
        return boards.stream().map(board -> {
            BoardDto boardDto = mapToDto(board);
            long pinCount = pinRepository.countByBoardId(board.getId());
            boardDto.setPinCount((int) pinCount);
            return boardDto;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateBoardDisplayOrder(Long userId, List<Long> boardIds) {
        // Validate all boards belong to the user
        List<Board> userBoards = boardRepository.findByUserId(userId);
        List<Long> userBoardIds = userBoards.stream().map(Board::getId).collect(Collectors.toList());
        
        for (Long boardId : boardIds) {
            if (!userBoardIds.contains(boardId)) {
                throw new IllegalArgumentException("Board with id " + boardId + " does not belong to user " + userId);
            }
        }
        
        // Update display order
        for (int i = 0; i < boardIds.size(); i++) {
            Long boardId = boardIds.get(i);
            Board board = userBoards.stream()
                    .filter(b -> b.getId().equals(boardId))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Board not found with id: " + boardId));
            
            board.setDisplayOrder(i);
            boardRepository.save(board);
        }
    }

    @Override
    public List<BoardDto> getCollaborativeBoardsByUserId(Long userId) {
        List<Board> boards = boardRepository.findCollaborativeBoardsByUserId(userId);
        return boards.stream().map(this::mapToDto).collect(Collectors.toList());
    }
    
    // Helper method to map Board entity to BoardDto
    private BoardDto mapToDto(Board board) {
        return BoardDto.builder()
                .id(board.getId())
                .title(board.getTitle())
                .description(board.getDescription())
                .coverImageUrl(board.getCoverImageUrl())
                .userId(board.getUserId())
                .isPrivate(board.isPrivate())
                .category(board.getCategory())
                .displayOrder(board.getDisplayOrder())
                .isCollaborative(board.isCollaborative())
                .pinCount((int) pinRepository.countByBoardId(board.getId()))
                .createdAt(board.getCreatedAt() != null ? board.getCreatedAt().format(DATE_FORMATTER) : null)
                .updatedAt(board.getUpdatedAt() != null ? board.getUpdatedAt().format(DATE_FORMATTER) : null)
                .build();
    }
    
    // Helper method to map BoardRequest to Board entity
    private Board mapToEntity(BoardRequest boardRequest) {
        return Board.builder()
                .title(boardRequest.getTitle())
                .description(boardRequest.getDescription())
                .coverImageUrl(boardRequest.getCoverImageUrl())
                .userId(boardRequest.getUserId())
                .isPrivate(boardRequest.isPrivate())
                .category(boardRequest.getCategory())
                .displayOrder(boardRequest.getDisplayOrder())
                .isCollaborative(boardRequest.isCollaborative())
                .build();
    }
}