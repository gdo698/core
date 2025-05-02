package com.core.erp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EmployeeManagementDTO {
    private Integer empId;
    private String empName;
    private String deptCode;
    private String empStatus;
    private String empPhone;
    private String empExt;
    private String empEmail;
    private LocalDate hireDate;
    private String empImg;
    
    // 프론트엔드와 호환을 위한 추가 필드
    private String deptName;
} 