package com.core.erp.dto.statistics;

import lombok.Data;

@Data
public class KpiStatsDTO {
    private int totalSales;          // 총 매출액
    private int totalOrders;         // 총 발주금액
    private int todaySalesQuantity;  // 오늘 판매 수량
    private int stockInCount;        // 금일 입고 수량
}
