package com.core.erp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomPrincipal {
    private final String loginId;
    private final Integer empId;
    private final Integer deptId;
    private final Integer storeId;
    private final String role;
}