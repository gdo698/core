package com.core.erp.service;

import com.core.erp.domain.EmployeeEntity;
import com.core.erp.dto.AuthResponse;
import com.core.erp.repository.EmployeeRepository;
import com.core.erp.util.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    /**
     * 사용자 로그인 처리 메소드
     * @param loginId 로그인 아이디
     * @param loginPwd 로그인 비밀번호
     * @return 인증 응답 객체 (토큰 및 사용자 정보 포함)
     */
    public AuthResponse login(String loginId, String loginPwd) {
        // 1. 사용자 ID로 사용자 검색
        EmployeeEntity user = employeeRepository.findByLoginId(loginId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 아이디입니다."));

        // 2. 이메일 인증 확인
        if (user.getEmailAuth() == null || user.getEmailAuth() != 1) {
            throw new RuntimeException("이메일 인증이 완료되지 않았습니다.");
        }

        // 3. 비밀번호 확인
        if (!passwordEncoder.matches(loginPwd, user.getLoginPwd())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        // 4. 부서 이름을 직접 권한으로 사용 (SecurityConfig와 일치)
        String role = "ROLE_" + user.getDepartment().getDeptName();

        // 5. 사용자 타입 결정 (부서명으로 구분)
        String userType = determineUserType(user.getDepartment().getDeptName());

        // 6. 지점명 가져오기
        String branchName = user.getStore() != null ? user.getStore().getStoreName() : null;

        // 7. 지점 ID 가져오기
        Integer storeId = user.getStore() != null ? user.getStore().getStoreId() : null;

        // 8. 사용자 이름 가져오기
        String name = user.getEmpName();

        // 9. JWT 토큰 생성
        String token = jwtProvider.createToken(loginId, role, userType, storeId, name, branchName);

        // 10. 인증 응답 객체 반환
        return new AuthResponse(token, branchName, user.getWorkType(), name, storeId);
    }

    /**
     * 부서명에 따른 사용자 타입 결정
     */
    private String determineUserType(String deptName) {
        if (deptName.startsWith("STORE") || deptName.startsWith("NON_STORE")) {
            return "STORE";
        } else if (deptName.startsWith("HQ") || deptName.startsWith("NON_HQ")) {
            return "HQ";
        } else if (deptName.equals("MASTER")) {
            return "MASTER";
        } else {
            return "USER"; // 기본값
        }
    }

}