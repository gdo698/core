package com.core.erp.dto.sales;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChartDataDTO {
    private String label;             // x축 라벨 (날짜, 카테고리명 등)
    private Double value;             // y축 값 (매출액, 거래 건수 등)
    
    @Builder.Default
    private Map<String, Object> additionalData = new HashMap<>(); // 추가 정보
} 