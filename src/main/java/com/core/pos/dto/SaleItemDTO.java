package com.core.pos.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SaleItemDTO {

    private Integer productId;
    private Integer salesQuantity;
    private Integer unitPrice;
    private Integer discountPrice;
    private Integer finalAmount;
    private Integer isPromo;
}
