package com.core.erp.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RegularInSettingsDTO {
    private Integer regularInDay;   
    private Integer regularInQuantity;
    private Boolean regularInActive;
} 