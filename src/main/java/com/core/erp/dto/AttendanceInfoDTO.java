package com.core.erp.dto;

import com.core.erp.dto.employee.EmployeeDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class AttendanceInfoDTO {
    private int attendanceDays;
    private int lateCount;
    private int absentCount;
    private int annualLeaveUsed;
    private int annualLeaveTotal;
    private int annualLeaveRemain;
    private String salaryDay;
    private EmployeeDTO employee;

    // Entity → DTO 변환 생성자
    public AttendanceInfoDTO(
            int attendanceDays,
            int lateCount,
            int absentCount,
            int annualLeaveUsed,
            int annualLeaveTotal,
            int annualLeaveRemain,
            String salaryDay,
            EmployeeDTO employee
    ) {
        this.attendanceDays = attendanceDays;
        this.lateCount = lateCount;
        this.absentCount = absentCount;
        this.annualLeaveUsed = annualLeaveUsed;
        this.annualLeaveTotal = annualLeaveTotal;
        this.annualLeaveRemain = annualLeaveRemain;
        this.salaryDay = salaryDay;
        this.employee = employee;
    }
}
