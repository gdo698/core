package com.core.erp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartTimerSearchDTO {
    private String partName;
    private Integer partStatus;
    private Integer partTimerId;
    private Integer page = 0;
    private Integer size = 10;
}