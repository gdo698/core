package com.core.springboot.dashboard.widget;

import com.core.springboot.dashboard.model.WidgetData;
import com.core.springboot.sales.service.SalesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SalesDailyWidget implements DashboardWidget {
    private final SalesService salesService;

    @Override
    public String getWidgetCode() {
        return "SALES_DAILY";
    }

    @Override
    public WidgetData getWidgetData(Long storeId) {
        LocalDate today = LocalDate.now();
        
        // 일일 매출 데이터 조회
        Map<String, Object> salesData = new HashMap<>();
        salesData.put("totalSales", salesService.getDailyTotalSales(storeId, today));
        salesData.put("transactionCount", salesService.getDailyTransactionCount(storeId, today));
        salesData.put("averageTransaction", salesService.getDailyAverageTransaction(storeId, today));
        
        // 카테고리별 매출
        salesData.put("categorySales", salesService.getDailyCategorySales(storeId, today));
        
        // 시간대별 매출
        salesData.put("hourlySales", salesService.getHourlySales(storeId, today));
        
        // 전일 대비 증감률
        salesData.put("growthRate", salesService.getDailyGrowthRate(storeId, today));

        return WidgetData.builder()
                .widgetCode(getWidgetCode())
                .data(salesData)
                .build();
    }
} 