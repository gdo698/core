package com.core.pos.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettlementPreviewDTO {
    private LocalDate settlementDate;
    private String settlementType;
    private Integer isManual;
    private String hqStatus;
    private Integer totalRevenue;
    private Integer discountTotal;
    private Integer refundTotal;
    private Integer finalAmount;
    private Integer transactionCount;
    private Integer refundCount;
}
