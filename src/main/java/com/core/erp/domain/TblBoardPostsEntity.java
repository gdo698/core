package com.core.erp.domain;

import com.core.erp.dto.TblBoardPostsDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_board_posts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TblBoardPostsEntity {

    @Id
    @Column(name = "post_id")
    private int postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emp_id", nullable = false)
    private EmployeeEntity employee;

    @Column(name = "board_type", nullable = false)
    private int boardType;

    @Column(name = "board_title", nullable = false, length = 255)
    private String boardTitle;

    @Column(name = "board_content", nullable = false, length = 255)
    private String boardContent;

    @Column(name = "board_created_at", nullable = false)
    private LocalDateTime boardCreatedAt;

    // DTO → Entity 변환 생성자
    public TblBoardPostsEntity(TblBoardPostsDTO dto) {
        this.postId = dto.getPostId();
        // employee는 별도 매핑 필요
        this.boardType = dto.getBoardType();
        this.boardTitle = dto.getBoardTitle();
        this.boardContent = dto.getBoardContent();
        this.boardCreatedAt = dto.getBoardCreatedAt();
    }
}