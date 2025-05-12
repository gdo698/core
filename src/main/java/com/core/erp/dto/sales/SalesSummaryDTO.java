package com.core.erp.dto.sales;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesSummaryDTO {
    private double totalSales;        // 총 매출
    private int totalTransactions;    // 총 거래 건수
    private double averageTransaction; // 평균 객단가
    private double previousPeriodSales; // 이전 기간 매출
    private double growthRate;        // 성장률
    private int totalDays;
    
    // 추가 데이터를 위한 Map
    @Builder.Default
    private Map<String, Object> additionalData = new HashMap<>();
    
    public Map<String, Object> getAdditionalData() {
        return additionalData;
    }
}