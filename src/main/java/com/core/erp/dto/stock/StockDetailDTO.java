package com.core.erp.dto.stock;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class StockDetailDTO {

    private String productName;
    private Long barcode;               // ← 수정됨
    private Integer promoStatus;        // ← 수정됨

    private int storeQuantity;
    private int warehouseQuantity;

    private int realStoreQuantity;
    private int realWarehouseQuantity;

    private String locationCode;        // ← 없을 경우 제거
}
