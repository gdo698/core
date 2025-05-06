package com.core.erp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// PartialItemDTO: 항목 하나당 처리 정보
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PartialItemDTO {
    private Integer itemId;       // 발주 상세 항목 ID
    private Integer inQuantity;   // 입고 수량
    private String reason;        // 입고 부족 사유 (nullable)
}
