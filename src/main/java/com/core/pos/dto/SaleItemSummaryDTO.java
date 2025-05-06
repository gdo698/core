package com.core.pos.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SaleItemSummaryDTO {

    private String productName;
    private Integer salesQuantity;
    private Integer isPromo;
}
