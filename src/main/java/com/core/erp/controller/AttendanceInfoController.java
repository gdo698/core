package com.core.erp.controller;

import com.core.erp.dto.AttendanceInfoDTO;
import com.core.erp.service.AttendanceInfoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hr")
public class AttendanceInfoController {

    @Autowired
    private AttendanceInfoService attendanceInfoService;

    @GetMapping("/my-page")
    public AttendanceInfoDTO getMyAttendanceInfo(HttpSession session) {
        System.out.println("✅ [my-page] 요청 도착");
        Integer empId = (Integer) session.getAttribute("empId"); // ✅ 세션에서 empId 꺼냄
        if (empId == null) {
            throw new RuntimeException("로그인 세션이 없습니다. (empId 없음)");
        }
        return attendanceInfoService.getEmployeeAttendanceInfo(empId); // ✅ Service에 위임
    }
}