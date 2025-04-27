package com.core.erp.domain;

import com.core.erp.dto.IssueLogDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "issue_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class IssueLogEntity {

    @Id
    @Column(name = "issue_id")
    private int issueId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private StoreEntity store;

    @Column(name = "issue_title", nullable = false, length = 50)
    private String issueTitle;

    @Column(name = "issue_desc", nullable = false, length = 225)
    private String issueDesc;

    @Column(name = "issue_type", nullable = false, length = 30)
    private String issueType;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // DTO → Entity 변환 생성자
    public IssueLogEntity(IssueLogDTO dto) {
        this.issueId = dto.getIssueId();
        // product, store는 별도 매핑 필요
        this.issueTitle = dto.getIssueTitle();
        this.issueDesc = dto.getIssueDesc();
        this.issueType = dto.getIssueType();
        this.createdAt = dto.getCreatedAt();
    }
}