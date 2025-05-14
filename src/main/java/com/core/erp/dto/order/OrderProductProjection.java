package com.core.erp.dto.order;

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