package com.core.erp.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductSalesDTO {
    private String productName;
    private int quantity;
    private String category;
}
