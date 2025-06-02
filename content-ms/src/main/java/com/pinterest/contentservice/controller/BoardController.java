package com.pinterest.contentservice.controller;

import com.pinterest.contentservice.dto.ApiResponse;
import com.pinterest.contentservice.dto.BoardDto;
import com.pinterest.contentservice.dto.BoardRequest;
import com.pinterest.contentservice.service.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/content/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<BoardDto>> createBoard(@Valid @RequestBody BoardRequest boardRequest) {
        BoardDto createdBoard = boardService.createBoard(boardRequest);
        return new ResponseEntity<>(ApiResponse.success("Board created successfully", createdBoard), HttpStatus.CREATED);
    }
    
    @GetMapping("/{boardId}")
    public ResponseEntity<ApiResponse<BoardDto>> getBoardById(@PathVariable Long boardId) {
        BoardDto boardDto = boardService.getBoardById(boardId);
        return ResponseEntity.ok(ApiResponse.success(boardDto));
    }
    
    @PutMapping("/{boardId}")
    public ResponseEntity<ApiResponse<BoardDto>> updateBoard(
            @PathVariable Long boardId,
            @Valid @RequestBody BoardRequest boardRequest) {
        BoardDto updatedBoard = boardService.updateBoard(boardId, boardRequest);
        return ResponseEntity.ok(ApiResponse.success("Board updated successfully", updatedBoard));
    }
    
    @DeleteMapping("/{boardId}")
    public ResponseEntity<ApiResponse<Void>> deleteBoard(@PathVariable Long boardId) {
        boardService.deleteBoard(boardId);
        return ResponseEntity.ok(ApiResponse.success("Board deleted successfully", null));
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<BoardDto>>> getBoardsByUserId(@PathVariable Long userId) {
        List<BoardDto> boards = boardService.getBoardsByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(boards));
    }
    
    @GetMapping("/user/{userId}/paged")
    public ResponseEntity<ApiResponse<Page<BoardDto>>> getBoardsByUserIdPaged(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "displayOrder") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<BoardDto> boards = boardService.getBoardsByUserId(userId, pageable);
        
        return ResponseEntity.ok(ApiResponse.success(boards));
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<BoardDto>>> searchBoards(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<BoardDto> boards = boardService.searchBoards(query, pageable);
        
        return ResponseEntity.ok(ApiResponse.success(boards));
    }
    
    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<List<BoardDto>>> getBoardsByCategory(@PathVariable String category) {
        List<BoardDto> boards = boardService.getBoardsByCategory(category);
        return ResponseEntity.ok(ApiResponse.success(boards));
    }
    
    @PutMapping("/user/{userId}/order")
    public ResponseEntity<ApiResponse<Void>> updateBoardDisplayOrder(
            @PathVariable Long userId,
            @RequestBody List<Long> boardIds) {
        boardService.updateBoardDisplayOrder(userId, boardIds);
        return ResponseEntity.ok(ApiResponse.success("Board display order updated successfully", null));
    }
    
    @GetMapping("/user/{userId}/collaborative")
    public ResponseEntity<ApiResponse<List<BoardDto>>> getCollaborativeBoardsByUserId(@PathVariable Long userId) {
        List<BoardDto> boards = boardService.getCollaborativeBoardsByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(boards));
    }
}