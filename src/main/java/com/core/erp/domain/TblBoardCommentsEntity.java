package com.core.erp.domain;

import com.core.erp.dto.TblBoardCommentsDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_board_comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TblBoardCommentsEntity {

    @Id
    @Column(name = "comment_id")
    private int commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private TblBoardPostsEntity post;

    @Column(name = "com_content", nullable = false, length = 255)
    private String comContent;

    @Column(name = "com_created_at", nullable = false)
    private LocalDateTime comCreatedAt;

    // DTO → Entity 변환 생성자
    public TblBoardCommentsEntity(TblBoardCommentsDTO dto) {
        this.commentId = dto.getCommentId();
        // post는 별도 매핑 필요
        this.comContent = dto.getComContent();
        this.comCreatedAt = dto.getComCreatedAt();
    }
}