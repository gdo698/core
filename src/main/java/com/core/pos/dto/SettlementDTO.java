package com.core.pos.dto;

import com.core.pos.domain.SalesSettlementEntity;
import com.core.pos.domain.SalesSettlementEntity.SettlementType;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettlementDTO {

    private Integer storeId;
    private LocalDate settlementDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer totalRevenue;
    private Integer discountTotal;
    private Integer refundTotal;
    private Integer finalAmount;
    private SettlementType settlementType;
    private Integer transactionCount;
    private Integer refundCount;

    // 변환용 정적 팩토리 메서드
    public static SettlementDTO from(SalesSettlementEntity entity) {
        return SettlementDTO.builder()
                .storeId(entity.getStoreId())
                .settlementDate(entity.getSettlementDate())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .totalRevenue(entity.getTotalRevenue())
                .discountTotal(entity.getDiscountTotal())
                .refundTotal(entity.getRefundTotal())
                .finalAmount(entity.getFinalAmount())
                .settlementType(entity.getSettlementType())
                .transactionCount(entity.getTransactionCount())
                .refundCount(entity.getRefundCount())
                .build();
    }
}

