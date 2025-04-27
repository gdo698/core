package com.core.erp.dto;

import com.core.erp.domain.TblBoardCommentsEntity;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TblBoardCommentsDTO {

    private int commentId;
    private Integer postId;
    private String comContent;
    private LocalDateTime comCreatedAt;

    private MultipartFile file;

    // Entity → DTO 변환 생성자
    public TblBoardCommentsDTO(TblBoardCommentsEntity entity) {
        this.commentId = entity.getCommentId();
        this.postId = entity.getPost() != null ? entity.getPost().getPostId() : null;
        this.comContent = entity.getComContent();
        this.comCreatedAt = entity.getComCreatedAt();
    }
}