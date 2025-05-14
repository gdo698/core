package com.core.erp.dto.stock;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
public class StockFlowSearchCondition {
    private Integer storeId;
    private Long productId;
    private String productName;
    private Integer flowType;
    private LocalDate startDate;
    private LocalDate endDate;
    private int page = 0;
    private int size = 10;
}