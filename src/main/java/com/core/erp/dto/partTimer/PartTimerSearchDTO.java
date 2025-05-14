package com.core.erp.dto.partTimer;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString

public class PartTimerSearchDTO {
    private String partName;
    private String position;
    private Integer partStatus;
    private Integer partTimerId;
    private Integer page = 0;
    private Integer size = 10;
}