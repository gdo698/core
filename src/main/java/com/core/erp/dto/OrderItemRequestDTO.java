package com.core.erp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemRequestDTO {
    private Integer productId;
    private Integer quantity;
    private Integer unitPrice;
}