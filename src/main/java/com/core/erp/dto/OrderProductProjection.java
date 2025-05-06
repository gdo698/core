package com.core.erp.dto;

public interface OrderProductProjection {
    Long getProductId();
    String getProductName();
    Integer getUnitPrice();
    Integer getStockQty();
    Integer getProStockLimit();
    Integer getIsPromo();
}
