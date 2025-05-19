package com.core.erp.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderProductDTO {
    private String productName;
    private int orderQuantity;
    private int orderAmount;
}
