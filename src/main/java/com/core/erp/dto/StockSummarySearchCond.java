package com.core.erp.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class StockSummarySearchCond {
    private Integer categoryId;
    private String productName;
    private String barcode;
    private int page = 0;
    private int size = 10;
}