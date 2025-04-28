package com.core.erp.service;

import com.core.erp.domain.EmployeeEntity;
import com.core.erp.domain.StoreEntity;  // 올바른 StoreEntity import
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

        // 로그인 시 Employee 엔티티 조회
        EmployeeEntity employee = employeeRepository.findByLoginIdAndLoginPwd(loginId, loginPwd);

        if (employee != null) {
            result.put("workType", 3);
            result.put("message", "로그인 성공");

            // employee 엔티티에서 store 객체 가져오기
            StoreEntity store = employee.getStore();  // store 필드로 StoreEntity 가져오기
            if (store != null) {
                result.put("branchName", store.getStoreName());  // storeName을 응답에 추가
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
