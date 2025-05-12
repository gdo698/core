package com.core.erp.dto;

import java.time.LocalDateTime;

public interface PurchaseOrderProjection {
    Long getOrderId();
    Integer getTotalQuantity();
    Integer getTotalAmount();
    LocalDateTime getOrderDate();
    Integer getOrderStatus();
}