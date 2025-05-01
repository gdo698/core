// CORE-ERP-POS-Backend/src/main/java/com/core/erp/service/BoardService.java
package com.core.erp.service;

import com.core.erp.domain.EmployeeEntity;
import com.core.erp.domain.TblBoardCommentsEntity;
import com.core.erp.domain.TblBoardPostsEntity;
import com.core.erp.dto.*;
import com.core.erp.repository.EmployeeRepository;
import com.core.erp.repository.TblBoardCommentsRepository;
import com.core.erp.repository.TblBoardPostsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final TblBoardPostsRepository boardPostsRepository;
    private final TblBoardCommentsRepository boardCommentsRepository;
    private final EmployeeRepository employeeRepository;

    // 게시판 타입별 게시글 목록 조회
    public List<BoardPostResponseDTO> getBoardPostsByType(int boardType) {
        List<Object[]> postsWithCommentStatus = boardPostsRepository.findByBoardTypeWithCommentStatus(boardType);
        List<BoardPostResponseDTO> result = new ArrayList<>();
        
        for (Object[] obj : postsWithCommentStatus) {
            TblBoardPostsEntity post = (TblBoardPostsEntity) obj[0];
            boolean hasComment = (boolean) obj[1];
            
            BoardPostResponseDTO dto = new BoardPostResponseDTO(post);
            dto.setHasComment(hasComment);
            
            if (hasComment) {
                List<TblBoardCommentsEntity> comments = boardCommentsRepository.findByPostOrderByComCreatedAtDesc(post);
                dto.setComments(comments.stream()
                    .map(comment -> {
                        BoardCommentResponseDTO commentDto = new BoardCommentResponseDTO(comment);
                        // 작성자 정보 설정
                        EmployeeEntity commentEmployee = employeeRepository.findById(post.getEmployee().getEmpId())
                            .orElse(null);
                        if (commentEmployee != null) {
                            commentDto.setEmpName(commentEmployee.getEmpName());
                        }
                        return commentDto;
                    })
                    .collect(Collectors.toList()));
            } else {
                dto.setComments(new ArrayList<>());
            }
            
            result.add(dto);
        }
        
        return result;
    }
    
    // 게시글 단일 조회
    public BoardPostResponseDTO getBoardPost(int postId) {
        TblBoardPostsEntity post = boardPostsRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        
        BoardPostResponseDTO dto = new BoardPostResponseDTO(post);
        boolean hasComment = boardCommentsRepository.existsByPost(post);
        dto.setHasComment(hasComment);
        
        if (hasComment) {
            List<TblBoardCommentsEntity> comments = boardCommentsRepository.findByPostOrderByComCreatedAtDesc(post);
            dto.setComments(comments.stream()
                .map(comment -> {
                    BoardCommentResponseDTO commentDto = new BoardCommentResponseDTO(comment);
                    // 작성자 정보 설정
                    EmployeeEntity commentEmployee = employeeRepository.findById(post.getEmployee().getEmpId())
                        .orElse(null);
                    if (commentEmployee != null) {
                        commentDto.setEmpName(commentEmployee.getEmpName());
                    }
                    return commentDto;
                })
                .collect(Collectors.toList()));
        } else {
            dto.setComments(new ArrayList<>());
        }
        
        return dto;
    }
    
    // 게시글 등록
    @Transactional
    public BoardPostResponseDTO createBoardPost(TblBoardPostsDTO dto, String loginId) {
        EmployeeEntity employee = employeeRepository.findByLoginId(loginId)
            .orElseThrow(() -> new RuntimeException("사원 정보를 찾을 수 없습니다."));
        
        TblBoardPostsEntity entity = new TblBoardPostsEntity(dto);
        entity.setEmployee(employee);
        entity.setBoardCreatedAt(LocalDateTime.now());
        
        TblBoardPostsEntity savedEntity = boardPostsRepository.save(entity);
        return new BoardPostResponseDTO(savedEntity);
    }
    
    // 게시글 수정
    @Transactional
    public BoardPostResponseDTO updateBoardPost(int postId, TblBoardPostsDTO dto, String loginId) {
        TblBoardPostsEntity entity = boardPostsRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        
        // 작성자 확인 또는 권한 확인 로직 추가
        EmployeeEntity employee = employeeRepository.findByLoginId(loginId)
            .orElseThrow(() -> new RuntimeException("사원 정보를 찾을 수 없습니다."));
            
        if (entity.getEmployee().getEmpId() != employee.getEmpId() &&
            !isManagerRole(employee)) {
            throw new RuntimeException("수정 권한이 없습니다.");
        }
        
        entity.setBoardTitle(dto.getBoardTitle());
        entity.setBoardContent(dto.getBoardContent());
        
        TblBoardPostsEntity updatedEntity = boardPostsRepository.save(entity);
        return new BoardPostResponseDTO(updatedEntity);
    }
    
    // 게시글 삭제
    @Transactional
    public void deleteBoardPost(int postId, String loginId) {
        TblBoardPostsEntity entity = boardPostsRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        
        // 작성자 확인 또는 권한 확인 로직 추가
        EmployeeEntity employee = employeeRepository.findByLoginId(loginId)
            .orElseThrow(() -> new RuntimeException("사원 정보를 찾을 수 없습니다."));
            
        if (entity.getEmployee().getEmpId() != employee.getEmpId() &&
            !isManagerRole(employee)) {
            throw new RuntimeException("삭제 권한이 없습니다.");
        }
        
        boardPostsRepository.delete(entity);
    }
    
    // 답변 등록
    @Transactional
    public BoardCommentResponseDTO createBoardComment(TblBoardCommentsDTO dto, String loginId) {
        TblBoardPostsEntity post = boardPostsRepository.findById(dto.getPostId())
            .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        
        // 권한 확인 로직 추가
        EmployeeEntity employee = employeeRepository.findByLoginId(loginId)
            .orElseThrow(() -> new RuntimeException("사원 정보를 찾을 수 없습니다."));
            
        if (!isManagerRole(employee)) {
            throw new RuntimeException("답변 등록 권한이 없습니다.");
        }
        
        TblBoardCommentsEntity entity = new TblBoardCommentsEntity(dto);
        entity.setPost(post);
        entity.setComCreatedAt(LocalDateTime.now());
        
        TblBoardCommentsEntity savedEntity = boardCommentsRepository.save(entity);
        
        BoardCommentResponseDTO responseDTO = new BoardCommentResponseDTO(savedEntity);
        responseDTO.setEmpName(employee.getEmpName());
        
        return responseDTO;
    }
    
    // 관리자 권한 확인 (HQ_BR, HQ_BR_M, MASTER)
    private boolean isManagerRole(EmployeeEntity employee) {
        if (employee.getDepartment() == null) return false;
        
        String deptName = employee.getDepartment().getDeptName();
        return deptName.equals("HQ_BR") || 
               deptName.equals("HQ_BR_M") || 
               deptName.equals("MASTER");
    }
}