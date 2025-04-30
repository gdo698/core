package com.core.erp.dto;

import lombok.Data;

@Data
public class EmployeeListDTO {
    private Long empId;
    private String empName;
    private String deptName;
    private String empRole;
    private String empStatus;
    private String hireDate;
    // 필요시 추가
} 