package com.core.erp.dto.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HourlySalesDTO {
    private String hour;
    private int sales;
}
