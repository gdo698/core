package com.core.erp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryCheckDTO {
    private Integer checkId;           // 실사 기록 ID
    private String productName;        // 상품명
    private Long barcode;              // 바코드
    private Integer prevQuantity;      // 실사 전 수량
    private Integer realQuantity;      // 실사 수량
    private Integer difference;        // 오차 (realQuantity - prevQuantity)
    private String checkReason;        // 사유
    private String partTimerName;      // 담당자 이름 (또는 ID)
    private LocalDateTime checkDate;   // 실사 일시
}
