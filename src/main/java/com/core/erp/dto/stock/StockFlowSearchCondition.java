package com.core.erp.dto.stock;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class StockFlowSearchCondition {
    private Integer storeId;
    private Long productId;
    private Integer flowType;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private int page = 0;
    private int size = 10;
}