package com.core.erp.dto.stock;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class StockFlowLogDTO {

    private Long flowId;

    private int productId;
    private String productName;
    private Long barcode;

    private Integer flowType;
    private String flowTypeLabel;

    private int quantity;
    private int beforeQuantity;
    private int afterQuantity;

    private String location;
    private String note;
    private String processedBy;
    private LocalDateTime flowDate;
}
