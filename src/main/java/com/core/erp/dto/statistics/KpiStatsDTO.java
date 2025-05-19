package com.core.erp.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KpiStatsDTO {
    private int totalSales;          // 총 매출액
    private int totalOrders;         // 총 발주금액
    private int todaySalesQuantity;  // 오늘 판매 수량
    private int stockInCount;        // 금일 입고 수량
}
