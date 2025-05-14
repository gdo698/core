package com.core.erp.dto.stock;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 카테고리별 재고 통계 정보를 담는 DTO
 * 파이 차트 등에 사용
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockCategoryStatDTO {
    // 카테고리 ID
    private int categoryId;
    
    // 카테고리 이름
    private String categoryName;
    
    // 해당 카테고리의 재고 수량 합계
    private long quantity;
    
    // 전체 재고 중 비율 (%) - 서비스에서 계산됨
    private double percentage;
} 