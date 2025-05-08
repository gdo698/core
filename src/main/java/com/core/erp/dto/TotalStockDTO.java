package com.core.erp.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class TotalStockDTO {

    private Integer storeId;
    private String storeName;
    private String productName;
    private Long barcode;
    private String categoryName;
    private Long storeQuantity;
    private Long warehouseQuantity;
    private Long totalQuantity;
    private LocalDateTime latestInDate;
    private String promoStatus;

    public TotalStockDTO(
            Integer storeId,
            String storeName,
            String productName,
            Long barcode,
            String categoryName,
            Long storeQuantity,
            Long warehouseQuantity,
            Long totalQuantity,
            LocalDateTime latestInDate,
            String promoStatus
    ) {
        this.storeId = storeId;
        this.storeName = storeName;
        this.productName = productName;
        this.barcode = barcode;
        this.categoryName = categoryName;
        this.storeQuantity = storeQuantity;
        this.warehouseQuantity = warehouseQuantity;
        this.totalQuantity = totalQuantity;
        this.latestInDate = latestInDate;
        this.promoStatus = promoStatus;
    }
}
