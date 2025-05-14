package com.core.erp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String branchName; // 지점명
    private int workType;
    private String name;       // emp_name
    private Integer storeId;

}

