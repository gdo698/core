package com.core.erp.service.widget;

import com.core.erp.dto.WidgetData;
import com.core.erp.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 점포 & 매출 현황 위젯 구현체
 */
@Component
@RequiredArgsConstructor
public class StoreOverviewWidget implements DashboardWidget {
    
    private final DashboardService dashboardService;
    
    @Override
    public String getWidgetCode() {
        return "STORE_OVERVIEW";
    }
    
    @Override
    public WidgetData getWidgetData(Long storeId) {
        // 대시보드 서비스에서 요약 정보 조회
        var summaryData = dashboardService.getDashboardSummary();
        
        Map<String, Object> data = new HashMap<>();
        
        // 점포 통계
        data.put("totalStores", summaryData.getTotalStores());
        data.put("newStores", summaryData.getNewStores());
        data.put("totalStoresGrowth", summaryData.getTotalStoresGrowth());
        data.put("newStoresGrowth", summaryData.getNewStoresGrowth());
        
        // 매출 통계
        data.put("dailySales", summaryData.getDailySales());
        data.put("dailySalesGrowth", summaryData.getDailySalesGrowth());
        data.put("monthlySales", summaryData.getMonthlySales());
        data.put("monthlySalesGrowth", summaryData.getMonthlySalesGrowth());
        
        return WidgetData.builder()
                .widgetCode(getWidgetCode())
                .data(data)
                .build();
    }
} 