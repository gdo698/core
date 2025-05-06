package com.core.erp.controller;

import com.core.erp.dto.CustomPrincipal;
import com.core.erp.dto.SalaryDTO;
import com.core.erp.dto.SalaryDetailDTO;
import com.core.erp.service.SalaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salary")
@RequiredArgsConstructor
@Slf4j
public class SalaryController {

    private final SalaryService salaryService;

    // ✅ 현재 로그인 사용자 정보 추출
    private CustomPrincipal getCurrentUser() {
        Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        return (CustomPrincipal) auth.getPrincipal();
    }

    // 📄 급여 리스트 조회 (월별 or 연도별)
    @GetMapping("/list")
    public ResponseEntity<Page<SalaryDTO>> getSalaryList(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String status,
            @RequestParam int year,
            @RequestParam(required = false) String month,
            @RequestParam(defaultValue = "monthly") String view,
            Pageable pageable
    ) {
        CustomPrincipal user = getCurrentUser();
        Page<SalaryDTO> result = salaryService.getSalaryList(
                name, status, year, month, view, user.getStoreId(), user.getRole(), pageable
        );
        return ResponseEntity.ok(result);
    }


    // 📄 급여 상세 (해당 아르바이트의 연도/월별 급여)
    @GetMapping("/detail/{id}")
    public ResponseEntity<List<SalaryDetailDTO>> getSalaryDetail(
            @PathVariable("id") int partTimerId,
            @RequestParam String view,
            @RequestParam int year,
            @RequestParam(required = false) String month
    ) {
        CustomPrincipal user = getCurrentUser();
        List<SalaryDetailDTO> result = salaryService.getSalaryDetail(partTimerId, view, year, month, user.getStoreId(), user.getRole());
        return ResponseEntity.ok(result);
    }

    // ✅ 급여 자동 생성 (본인 매장 기준)
    @PostMapping("/generate")
    public ResponseEntity<?> generateSalary(@RequestParam String yearMonth) {
        CustomPrincipal user = getCurrentUser();

        boolean alreadyExists = salaryService.existsSalaryForMonth(yearMonth, user.getStoreId());
        if (alreadyExists) {
            return ResponseEntity
                    .status(409)
                    .body("이미 해당 월의 급여가 생성되었습니다.");  // 메시지 포함 응답
        }

        salaryService.generateSalary(yearMonth, user.getStoreId());
        return ResponseEntity.ok("급여가 성공적으로 생성되었습니다.");
    }

}