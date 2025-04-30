package com.core.erp.controller;

import com.core.erp.domain.EmployeeEntity;
import com.core.erp.repository.EmployeeRepository;
import com.core.erp.security.JwtTokenProvider;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final EmployeeRepository employeeRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        EmployeeEntity employee = employeeRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 아이디입니다."));
        if (!passwordEncoder.matches(request.getLoginPwd(), employee.getLoginPwd())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }
        String token = jwtTokenProvider.createToken(employee);
        return ResponseEntity.ok(Map.of(
                "token", token,
                "empId", employee.getEmpId(),
                "deptId", employee.getDepartment().getDeptId(),
                "empName", employee.getEmpName(),
                "deptName", employee.getDepartment().getDeptName()
        ));
    }

    @Data
    public static class LoginRequest {
        private String loginId;
        private String loginPwd;
    }
}