package com.core.erp.dto.stock;

public interface StoreStockProjection {
    Long getProductId();
    String getProductName();
    Long getBarcode();
    String getCategoryName();

}