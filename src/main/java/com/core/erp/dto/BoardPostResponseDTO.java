// CORE-ERP-POS-Backend/src/main/java/com/core/erp/dto/BoardPostResponseDTO.java
package com.core.erp.dto;

import com.core.erp.domain.TblBoardPostsEntity;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BoardPostResponseDTO {
    private int postId;
    private int empId;
    private String empName;
    private int boardType;
    private String boardTitle;
    private String boardContent;
    private LocalDateTime boardCreatedAt;
    private boolean hasComment; // 답변 여부
    private List<BoardCommentResponseDTO> comments; // 답변 목록
    
    public BoardPostResponseDTO(TblBoardPostsEntity entity) {
        this.postId = entity.getPostId();
        this.empId = entity.getEmployee().getEmpId();
        this.empName = entity.getEmployee().getEmpName();
        this.boardType = entity.getBoardType();
        this.boardTitle = entity.getBoardTitle();
        this.boardContent = entity.getBoardContent();
        this.boardCreatedAt = entity.getBoardCreatedAt();
    }
}