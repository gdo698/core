package com.core.springboot.dashboard.widget;

import com.core.springboot.dashboard.model.WidgetData;
import com.core.springboot.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OperationInventoryWidget implements DashboardWidget {
    private final InventoryService inventoryService;

    @Override
    public String getWidgetCode() {
        return "OPERATION_INVENTORY";
    }

    @Override
    public WidgetData getWidgetData(Long storeId) {
        Map<String, Object> inventoryData = new HashMap<>();
        
        // 전체 재고 현황
        inventoryData.put("totalStockValue", inventoryService.getTotalStockValue(storeId));
        inventoryData.put("totalItems", inventoryService.getTotalItems(storeId));
        
        // 재고 상태
        inventoryData.put("lowStockItems", inventoryService.getLowStockItems(storeId));
        inventoryData.put("outOfStockItems", inventoryService.getOutOfStockItems(storeId));
        
        // 카테고리별 재고 현황
        inventoryData.put("categoryStock", inventoryService.getCategoryStock(storeId));
        
        // 재고 회전율
        inventoryData.put("turnoverRate", inventoryService.getTurnoverRate(storeId));
        
        // 유통기한 임박 상품
        inventoryData.put("expiringSoon", inventoryService.getExpiringSoonItems(storeId));

        return WidgetData.builder()
                .widgetCode(getWidgetCode())
                .data(inventoryData)
                .build();
    }
} 