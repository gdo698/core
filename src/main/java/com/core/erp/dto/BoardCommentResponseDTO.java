// CORE-ERP-POS-Backend/src/main/java/com/core/erp/dto/BoardCommentResponseDTO.java
package com.core.erp.dto;

import com.core.erp.domain.TblBoardCommentsEntity;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BoardCommentResponseDTO {
    private int commentId;
    private int postId;
    private String comContent;
    private LocalDateTime comCreatedAt;
    private String empName; // 답변 작성자 이름
    
    public BoardCommentResponseDTO(TblBoardCommentsEntity entity) {
        this.commentId = entity.getCommentId();
        this.postId = entity.getPost().getPostId();
        this.comContent = entity.getComContent();
        this.comCreatedAt = entity.getComCreatedAt();
        // 답변 작성자 정보는 별도로 설정해야 함
    }
}