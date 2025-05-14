package com.core.erp.dto.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderProductResponseDTO {

    private Integer productId;
    private String productName;
    private Long barcode;
    private String categoryName;
    private Integer unitPrice;
    private Integer stockQty;
    private Integer proStockLimit;
    private Integer isPromo;
}
