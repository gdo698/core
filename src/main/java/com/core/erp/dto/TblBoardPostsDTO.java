package com.core.erp.dto;

import com.core.erp.domain.TblBoardPostsEntity;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TblBoardPostsDTO {

    private int postId;
    private Integer empId; // FK (id만 관리)
    private int boardType;
    private String boardTitle;
    private String boardContent;
    private LocalDateTime boardCreatedAt;

    private MultipartFile file;

    // Entity → DTO 변환 생성자
    public TblBoardPostsDTO(TblBoardPostsEntity entity) {
        this.postId = entity.getPostId();
        this.empId = entity.getEmployee() != null ? entity.getEmployee().getEmpId() : null;
        this.boardType = entity.getBoardType();
        this.boardTitle = entity.getBoardTitle();
        this.boardContent = entity.getBoardContent();
        this.boardCreatedAt = entity.getBoardCreatedAt();
    }
}