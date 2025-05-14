package com.core.erp.dto.order;

import java.time.LocalDateTime;

public interface PurchaseOrderProjection {
    Long getOrderId();
    Integer getTotalQuantity();
    Integer getTotalAmount();
    LocalDateTime getOrderDate();
    Integer getOrderStatus();
}