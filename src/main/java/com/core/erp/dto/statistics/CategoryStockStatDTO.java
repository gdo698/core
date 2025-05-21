package com.core.erp.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryStockStatDTO {
    private int categoryId;
    private String categoryName;
    private double percentage;
    private int categoryFilter;
} 