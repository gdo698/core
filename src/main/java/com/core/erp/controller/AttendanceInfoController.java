package com.core.erp.controller;

import com.core.erp.dto.AttendanceInfoDTO;
import com.core.erp.service.AttendanceInfoService;
import com.core.erp.domain.EmployeeEntity;
import com.core.erp.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hr")
@RequiredArgsConstructor
public class AttendanceInfoController {

    private final AttendanceInfoService attendanceInfoService;
    private final EmployeeRepository employeeRepository;

    @GetMapping("/my-page")
    public AttendanceInfoDTO getMyAttendanceInfo(Authentication authentication) {
        String loginId = authentication.getName(); // JWT에서 loginId 추출
        EmployeeEntity emp = employeeRepository.findByLoginId(loginId);
        if (emp == null) {
            throw new RuntimeException("사원 정보 없음");
        }
        return attendanceInfoService.getEmployeeAttendanceInfo(emp.getEmpId());
    }
}