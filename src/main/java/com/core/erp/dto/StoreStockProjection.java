package com.core.erp.dto;

import java.time.LocalDateTime;

public interface StoreStockProjection {
    Long getProductId();
    String getProductName();
    Long getBarcode();
    String getCategoryName();

}