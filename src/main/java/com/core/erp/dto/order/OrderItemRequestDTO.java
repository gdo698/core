package com.core.erp.dto.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemRequestDTO {
    private Integer productId;
    private String productName;
    private Integer quantity;
    private Integer unitPrice;
}