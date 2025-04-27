package com.core.erp.dto;

import com.core.erp.domain.IssueLogEntity;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class IssueLogDTO {

    private int issueId;
    private Integer productId; // FK (nullable)
    private Integer storeId; // FK (id만 관리)
    private String issueTitle;
    private String issueDesc;
    private String issueType;
    private LocalDateTime createdAt;

    // Entity → DTO 변환 생성자
    public IssueLogDTO(IssueLogEntity entity) {
        this.issueId = entity.getIssueId();
        this.productId = entity.getProduct() != null ? entity.getProduct().getProductId() : null;
        this.storeId = entity.getStore() != null ? entity.getStore().getStoreId() : null;
        this.issueTitle = entity.getIssueTitle();
        this.issueDesc = entity.getIssueDesc();
        this.issueType = entity.getIssueType();
        this.createdAt = entity.getCreatedAt();
    }
}