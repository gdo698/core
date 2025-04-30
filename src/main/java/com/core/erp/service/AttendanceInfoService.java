package com.core.erp.service;

import com.core.erp.domain.*;
import com.core.erp.dto.*;
import com.core.erp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AttendanceInfoService {

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private AttendanceRepository attendanceRepository;
    @Autowired
    private AnnualLeaveRepository annualLeaveRepository;
    @Autowired
    private SalaryRepository salaryRepository;

    public AttendanceInfoDTO getEmployeeAttendanceInfo(int empId) {
        EmployeeEntity empEntity = employeeRepository.findById(empId)
            .orElseThrow(() -> new RuntimeException("사원 정보 없음"));
        EmployeeDTO emp = new EmployeeDTO(empEntity);

        List<AttendanceEntity> attendanceEntities = attendanceRepository.findByEmployee_EmpId(empId);
        List<AttendanceDTO> attendances = attendanceEntities.stream().map(AttendanceDTO::new).collect(Collectors.toList());
        int attendanceDays = attendances.size();
        int lateCount = (int) attendances.stream().filter(a -> a.getAttendStatus() == 2).count();
        int absentCount = (int) attendances.stream().filter(a -> a.getAttendStatus() == 3).count();

        Optional<AnnualLeaveEntity> leaveEntityOpt = annualLeaveRepository.findTopByEmployee_EmpIdOrderByYearDesc(empId);
        AnnualLeaveDTO leave = leaveEntityOpt.map(AnnualLeaveDTO::new).orElse(null);
        int annualLeaveUsed = leave != null ? leave.getUsedDays() : 0;
        int annualLeaveTotal = leave != null ? leave.getTotalDays() : 0;
        int annualLeaveRemain = leave != null ? leave.getRemDays() : 0;

        Optional<SalaryEntity> salaryEntityOpt = salaryRepository.findTopByEmployee_EmpIdOrderByPayDateDesc(empId);
        SalaryDTO salary = salaryEntityOpt.map(SalaryDTO::new).orElse(null);
        String salaryDay = (salary != null && salary.getPayDate() != null) ? salary.getPayDate().toString() : "매월 5일";

        AttendanceInfoDTO attendInfoDto = new AttendanceInfoDTO();
        attendInfoDto.setEmployee(emp);
        attendInfoDto.setAttendanceDays(attendanceDays);
        attendInfoDto.setLateCount(lateCount);
        attendInfoDto.setAbsentCount(absentCount);
        attendInfoDto.setAnnualLeaveUsed(annualLeaveUsed);
        attendInfoDto.setAnnualLeaveTotal(annualLeaveTotal);
        attendInfoDto.setAnnualLeaveRemain(annualLeaveRemain);
        attendInfoDto.setSalaryDay(salaryDay);

        return attendInfoDto;
    }
} 