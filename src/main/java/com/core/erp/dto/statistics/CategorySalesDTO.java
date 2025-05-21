package com.core.erp.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategorySalesDTO {
    private String category;
    private int totalSales;
    private int quantity;
    private int transactionCount;
}
