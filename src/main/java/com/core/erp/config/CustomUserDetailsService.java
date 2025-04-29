package com.core.erp.config;

import com.core.erp.domain.EmployeeEntity;
import com.core.erp.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final EmployeeRepository employeeRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. loginId로 Employee 조회
        EmployeeEntity employee = employeeRepository.findByLoginId(username);

        // 2. null 체크로 예외 처리
        if (employee == null) {
            throw new UsernameNotFoundException("존재하지 않는 사용자입니다.");
        }

        // 3. storeId, departId 추출
        Integer storeId = employee.getStore() != null ? employee.getStore().getStoreId() : null;
        Integer departId = employee.getDepartment() != null ? employee.getDepartment().getDeptId() : null;

        // 4. 권한 결정
        String role = determineRole(departId);
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

        // 5. CustomUserDetails 생성
        return new CustomUserDetails(
                storeId,
                departId,
                employee.getLoginId(),
                employee.getLoginPwd(),
                authorities
        );
    }

    // ✨ departId로 role을 구분하는 메소드
    private String determineRole(Integer departId) {
        if (departId == null) {
            throw new RuntimeException("부서 정보가 없습니다.");
        }
        if (departId >= 1 && departId <= 10) {
            return "ROLE_HQ";   // 본사
        } else if (departId == 13) {
            return "ROLE_OWNER"; // 점주
        } else {
            throw new RuntimeException("허용되지 않은 부서입니다.");
        }
    }
}