package com.core.erp.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSalesDTO {
    private String productName;
    private int quantity;
    private int totalAmount;
    private String category;
}
