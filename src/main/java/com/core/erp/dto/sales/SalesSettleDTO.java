package com.core.erp.dto.sales;

import com.core.erp.domain.SalesSettleEntity;
import com.core.erp.domain.SalesSettleEntity.SettlementType;
import com.core.erp.domain.SalesSettleEntity.HqStatus;
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
    private Integer refundTotal;
    private Integer finalAmount;
    private SettlementType settlementType;
    private Integer transactionCount;
    private Integer refundCount;
    private LocalDateTime hqSentAt;
    private HqStatus hqStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Entity → DTO 변환 생성자
    public SalesSettleDTO(SalesSettleEntity entity) {
        this.settlementId = entity.getSettlementId();
        this.storeId = entity.getStore() != null ? entity.getStore().getStoreId() : null;
        this.settlementDate = entity.getSettlementDate();
        this.totalRevenue = entity.getTotalRevenue();
        this.discountTotal = entity.getDiscountTotal();
        this.refundTotal = entity.getRefundTotal();
        this.finalAmount = entity.getFinalAmount();
        this.settlementType = entity.getSettlementType();
        this.transactionCount = entity.getTransactionCount();
        this.refundCount = entity.getRefundCount();
        this.hqSentAt = entity.getHqSentAt();
        this.hqStatus = entity.getHqStatus();
        this.createdAt = entity.getCreatedAt();
        this.updatedAt = entity.getUpdatedAt();
    }
}
