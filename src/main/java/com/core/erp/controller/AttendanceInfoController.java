package com.core.erp.controller;

import com.core.erp.dto.AttendanceInfoDTO;
import com.core.erp.dto.EmployeeDTO;
import com.core.erp.service.AttendanceInfoService;
import com.core.erp.domain.EmployeeEntity;
import com.core.erp.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/hr")
@RequiredArgsConstructor
public class AttendanceInfoController {

    private final AttendanceInfoService attendanceInfoService;
    private final EmployeeRepository employeeRepository;

    @GetMapping("/my-page")
    public Map<String, Object> getMyAttendanceInfo(Authentication authentication) {
        String loginId = authentication.getName(); // JWT에서 loginId 추출
        EmployeeEntity emp = employeeRepository.findByLoginId(loginId)
                .orElseThrow(() -> new RuntimeException("사원 정보 없음"));

        // 근태 정보 조회
        AttendanceInfoDTO attendanceInfo = attendanceInfoService.getEmployeeAttendanceInfo(emp.getEmpId());

        // 사원 정보 DTO 변환
        EmployeeDTO employeeDTO = new EmployeeDTO(emp);

        // 응답 데이터 구성
        Map<String, Object> response = new HashMap<>();

        // 사원 기본 정보 추가
        response.put("empId", employeeDTO.getEmpId());
        response.put("empName", employeeDTO.getEmpName());
        response.put("empRole", employeeDTO.getEmpRole());
        response.put("empPhone", employeeDTO.getEmpPhone());
        response.put("empAddr", employeeDTO.getEmpAddr());
        response.put("empBirth", employeeDTO.getEmpBirth());
        response.put("empBank", employeeDTO.getEmpBank());
        response.put("empAcount", employeeDTO.getEmpAcount());
        response.put("empImg", employeeDTO.getEmpImg());
        response.put("hireDate", employeeDTO.getHireDate());

        // 근태 정보 추가
        if (attendanceInfo != null) {
            response.put("attendanceDays", attendanceInfo.getAttendanceDays());
            response.put("lateCount", attendanceInfo.getLateCount());
            response.put("absentCount", attendanceInfo.getAbsentCount());
            response.put("annualLeaveRemain", attendanceInfo.getAnnualLeaveRemain());
            response.put("annualLeaveTotal", attendanceInfo.getAnnualLeaveTotal());
            response.put("salaryDay", attendanceInfo.getSalaryDay());
        } else {
            // 기본값 설정
            response.put("attendanceDays", 0);
            response.put("lateCount", 0);
            response.put("absentCount", 0);
            response.put("annualLeaveRemain", 0);
            response.put("annualLeaveTotal", 0);
            response.put("salaryDay", "매월 25일");
        }

        // 부서 정보 추가
        if (emp.getDepartment() != null) {
            response.put("deptId", emp.getDepartment().getDeptId());
            response.put("deptName", emp.getDepartment().getDeptName());
        }

        // 지점 정보 추가
        if (emp.getStore() != null) {
            response.put("storeId", emp.getStore().getStoreId());
            response.put("storeName", emp.getStore().getStoreName());
        }

        return response;
    }
}