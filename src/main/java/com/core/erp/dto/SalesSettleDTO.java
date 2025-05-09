package com.core.erp.dto;

import com.core.erp.domain.SalesSettleEntity;
import com.core.erp.domain.SalesSettleEntity.SettlementType;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesSettleDTO {

    private Integer settlementId;
    private Integer storeId;
    private LocalDate settlementDate;
    private Integer totalRevenue;
    private Integer discountTotal;
    private Integer finalAmount;
    private SettlementType settlementType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Entity → DTO 변환 생성자
    public SalesSettleDTO(SalesSettleEntity entity) {
        this.settlementId = entity.getSettlementId();
        this.storeId = entity.getStore() != null ? entity.getStore().getStoreId() : null;
        this.settlementDate = entity.getSettlementDate();
        this.totalRevenue = entity.getTotalRevenue();
        this.discountTotal = entity.getDiscountTotal();
        this.finalAmount = entity.getFinalAmount();
        this.settlementType = entity.getSettlementType();
        this.createdAt = entity.getCreatedAt();
        this.updatedAt = entity.getUpdatedAt();
    }
}
