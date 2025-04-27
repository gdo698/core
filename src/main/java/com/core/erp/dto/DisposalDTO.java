package com.core.erp.dto;

import com.core.erp.domain.DisposalEntity;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DisposalDTO {

    private int disposalId;
    private Integer stockId; // FK (id만 관리)
    private LocalDateTime disposalDate;
    private int disposalQuantity;
    private String processedBy;
    private int totalLossAmount;
    private String disposalReason;

    private MultipartFile file;

    // Entity → DTO 변환 생성자
    public DisposalDTO(DisposalEntity entity) {
        this.disposalId = entity.getDisposalId();
        this.stockId = entity.getStoreStock() != null ? entity.getStoreStock().getStockId() : null;
        this.disposalDate = entity.getDisposalDate();
        this.disposalQuantity = entity.getDisposalQuantity();
        this.processedBy = entity.getProcessedBy();
        this.totalLossAmount = entity.getTotalLossAmount();
        this.disposalReason = entity.getDisposalReason();
    }
}