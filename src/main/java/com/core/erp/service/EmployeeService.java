package com.core.erp.service;

import com.core.erp.domain.EmployeeEntity;
import com.core.erp.domain.StoreEntity;
import com.core.erp.repository.EmployeeRepository;
import com.core.erp.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final StoreRepository storeRepository;

    public Map<String, Object> login(String loginId, String loginPwd) {
        Map<String, Object> result = new HashMap<>();

        // Optional 처리
        EmployeeEntity employee = employeeRepository.findByLoginId(loginId)
                .orElse(null);
        if (employee != null && employee.getLoginPwd().equals(loginPwd)) {
            result.put("workType", 3);
            result.put("message", "로그인 성공");
            result.put("name", employee.getEmpName());

            StoreEntity store = employee.getStore();
            if (store != null) {
                result.put("branchName", store.getStoreName());
            } else {
                result.put("branchName", "지점명 없음");
            }
        } else {
            result.put("workType", 0);
            result.put("message", "로그인 실패");
        }

        return result;
    }
}