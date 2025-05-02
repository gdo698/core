package com.core.erp.controller;

import com.core.erp.domain.EmployeeEntity;
import com.core.erp.repository.EmployeeRepository;
import com.core.erp.security.JwtTokenProvider;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
        
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("empId", employee.getEmpId());
        response.put("deptId", employee.getDepartment().getDeptId());
        response.put("empName", employee.getEmpName());
        response.put("deptName", employee.getDepartment().getDeptName());
        response.put("role", "ROLE_" + jwtTokenProvider.mapDeptIdToRole(employee.getDepartment().getDeptId()));
        
        // 매장 정보가 있는 경우 추가
        if (employee.getStore() != null) {
            response.put("storeId", employee.getStore().getStoreId());
            response.put("storeName", employee.getStore().getStoreName());
        } else {
            response.put("storeId", null);
            response.put("storeName", null);
        }
        
        return ResponseEntity.ok(response);
    }

    @Data
    public static class LoginRequest {
        private String loginId;
        private String loginPwd;
    }
}