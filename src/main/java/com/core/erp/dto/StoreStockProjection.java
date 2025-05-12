package com.core.erp.dto;

import java.time.LocalDateTime;

public interface StoreStockProjection {
    Long getProductId();
    String getProductName();
    Long getBarcode();
    Integer getStoreQuantity();       // 매장 진열 재고
    Integer getWarehouseQuantity();   // 창고 재고
    Integer getTotalQuantity();       // 매장 + 창고 합계
    String getCategoryName();
    LocalDateTime getLatestInDate();
    String getPromoStatus();
}