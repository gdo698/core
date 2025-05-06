package com.core.pos.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SaleRequestDTO {

    private Integer storeId;
    private Integer empId;
    private String paymentMethod;
    private List<SaleItemDTO> itemList;
}

