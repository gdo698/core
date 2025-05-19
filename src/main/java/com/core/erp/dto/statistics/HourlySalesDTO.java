package com.core.erp.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HourlySalesDTO {
    private String hour;
    private int quantity;
    private int total;
}
