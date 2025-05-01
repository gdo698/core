// CORE-ERP-POS-Backend/src/main/java/com/core/erp/controller/BoardController.java
package com.core.erp.controller;

import com.core.erp.dto.*;
import com.core.erp.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/headquarters/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    // 게시판 타입별 게시글 목록 조회
    @GetMapping("/{boardType}")
    public ResponseEntity<List<BoardPostResponseDTO>> getBoardPosts(@PathVariable int boardType) {
        return ResponseEntity.ok(boardService.getBoardPostsByType(boardType));
    }
    
    // 게시글 단일 조회
    @GetMapping("/post/{postId}")
    public ResponseEntity<BoardPostResponseDTO> getBoardPost(@PathVariable int postId) {
        return ResponseEntity.ok(boardService.getBoardPost(postId));
    }
    
    // 게시글 등록
    @PostMapping("/write")
    public ResponseEntity<BoardPostResponseDTO> createBoardPost(
            @RequestBody TblBoardPostsDTO dto,
            Authentication authentication) {
        String loginId = authentication.getName();
        return ResponseEntity.ok(boardService.createBoardPost(dto, loginId));
    }
    
    // 게시글 수정
    @PutMapping("/write/{postId}")
    public ResponseEntity<BoardPostResponseDTO> updateBoardPost(
            @PathVariable int postId,
            @RequestBody TblBoardPostsDTO dto,
            Authentication authentication) {
        String loginId = authentication.getName();
        return ResponseEntity.ok(boardService.updateBoardPost(postId, dto, loginId));
    }
    
    // 게시글 삭제
    @DeleteMapping("/write/{postId}")
    public ResponseEntity<Void> deleteBoardPost(
            @PathVariable int postId,
            Authentication authentication) {
        String loginId = authentication.getName();
        boardService.deleteBoardPost(postId, loginId);
        return ResponseEntity.noContent().build();
    }
    
    // 게시글 답변 등록
    @PostMapping("/comment")
    public ResponseEntity<BoardCommentResponseDTO> createBoardComment(
            @RequestBody TblBoardCommentsDTO dto,
            Authentication authentication) {
        String loginId = authentication.getName();
        return ResponseEntity.ok(boardService.createBoardComment(dto, loginId));
    }
}