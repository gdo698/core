package com.core.erp.controller;

import com.core.erp.dto.CustomPrincipal;
import com.core.erp.dto.salary.SalaryDTO;
import com.core.erp.service.SalaryHQService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hr")
@RequiredArgsConstructor
@Slf4j
public class SalaryHQController {

    private final SalaryHQService salaryHQService;

    // 현재 로그인 사용자 정보 추출
    private CustomPrincipal getCurrentUser() {
        Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        return (CustomPrincipal) auth.getPrincipal();
    }

    // 본사 직원 급여 내역 조회
    @GetMapping("/my-salary")
    public ResponseEntity<List<SalaryDTO>> getMySalary() {
        CustomPrincipal user = getCurrentUser();
        List<SalaryDTO> result = salaryHQService.getMySalary(user.getEmpId());
        return ResponseEntity.ok(result);
    }
} 