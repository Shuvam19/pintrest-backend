package com.pinterest.contentservice.service;

import com.pinterest.contentservice.dto.BoardDto;
import com.pinterest.contentservice.dto.BoardRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BoardService {
    
    // Create a new board
    BoardDto createBoard(BoardRequest boardRequest);
    
    // Get a board by ID
    BoardDto getBoardById(Long boardId);
    
    // Update an existing board
    BoardDto updateBoard(Long boardId, BoardRequest boardRequest);
    
    // Delete a board
    void deleteBoard(Long boardId);
    
    // Get all boards by user ID
    List<BoardDto> getBoardsByUserId(Long userId);
    
    // Get boards by user ID with pagination
    Page<BoardDto> getBoardsByUserId(Long userId, Pageable pageable);
    
    // Search boards by keyword
    Page<BoardDto> searchBoards(String searchTerm, Pageable pageable);
    
    // Get boards by category
    List<BoardDto> getBoardsByCategory(String category);
    
    // Update board display order
    void updateBoardDisplayOrder(Long userId, List<Long> boardIds);
    
    // Get collaborative boards by user ID
    List<BoardDto> getCollaborativeBoardsByUserId(Long userId);
}