package com.core.erp.dto.sales;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesSummaryDTO {
    private double totalSales;        // 총 매출
    private int totalTransactions;    // 총 거래 건수
    private double averageTransaction; // 평균 객단가
    private double previousPeriodSales; // 이전 기간 매출
    private double growthRate;        // 성장률
} 