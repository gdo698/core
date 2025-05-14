package com.core.erp.dto.stock;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 재고 상태별 요약 정보를 담는 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockStatusSummaryDTO {
    // 긴급 재고 상품 수 (재고 부족 상품)
    private long dangerCount;
    
    // 경고 재고 상품 수 (유통기한 임박 상품)
    private long warningCount;
    
    // 정상 재고 상품 수
    private long normalCount;
    
    // 총 재고 상품 수
    private long totalCount;
} 