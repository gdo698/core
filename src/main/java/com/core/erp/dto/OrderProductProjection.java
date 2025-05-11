package com.core.erp.dto;

public interface OrderProductProjection {
    Integer getProductId();
    String getProductName();
    Long getBarcode();
    String getCategoryName();
    Integer getUnitPrice();
    Integer getStockQty();
    Integer getProStockLimit();
    Integer getIsPromo();
}