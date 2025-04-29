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

    public AuthResponse login(String loginId, String loginPwd) {
        EmployeeEntity user = employeeRepository.findByLoginId(loginId);

        // 1. 아이디로 사용자 조회
        if (user == null) {
            throw new RuntimeException("존재하지 않는 아이디입니다.");
        }

        // 2. 이메일 인증 여부 확인
        if (user.getEmailAuth() == null || user.getEmailAuth() != 1) {
            throw new RuntimeException("이메일 인증이 완료되지 않았습니다.");
        }

        // 3. 비밀번호 확인
        if (!passwordEncoder.matches(loginPwd, user.getLoginPwd())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        // 4. 사용자 role 결정
        String role = determineRole(user.getDepartment().getDeptId(), user.getEmpRole());

        // 5. 사용자 타입 결정 (본사 or 점주)
        String userType = determineUserType(user.getDepartment().getDeptId());

        // 6. 사용자 정보에 workType 추가
        int workType = determineWorkType(user.getDepartment().getDeptId(), user.getEmpRole());

        // 7. 토큰 발급
        String token = jwtProvider.createToken(user.getLoginId(), role, userType);

        // 8. store 객체에서 지점명 가져오기 (storeName)
        String branchName = user.getStore() != null ? user.getStore().getStoreName() : null;

        // 9. AuthResponse 객체 생성 후 반환
        return new AuthResponse(token, branchName, workType);
    }

    // 부서에 따른 역할 결정
    private String determineRole(Integer departId, String empRole) {
        if (departId == null) {
            throw new RuntimeException("부서 정보가 존재하지 않습니다.");
        }

        switch (departId) {
            case 1: // 인사팀
                if ("차장".equals(empRole) || "부장".equals(empRole)) {
                    return "ROLE_HR_APPROVER";
                } else {
                    return "ROLE_HR";
                }
            case 2: // 상품관리팀
                return "ROLE_PRODUCT_MANAGER";
            case 3: // 지점관리팀
                if ("차장".equals(empRole) || "부장".equals(empRole)) {
                    return "ROLE_BRANCH_EDITOR";
                } else {
                    return "ROLE_BRANCH_VIEWER";
                }
            case 5: // 점주
                return "ROLE_OWNER";
            default:
                throw new RuntimeException("알 수 없는 부서입니다.");
        }
    }

    // 부서 ID에 따라 사용자 유형 결정
    private String determineUserType(Integer deptId) {
        if (deptId == 5) {  // 점주일 경우
            return "OWNER";  // 점주
        }
        return "HEAD_OFFICE";  // 본사 관리자
    }

    // 부서 ID와 직급에 따른 workType 결정
    private int determineWorkType(Integer deptId, String empRole) {
        if (deptId == 5) {  // 점주인 경우
            return 3;
        }
        // 본사 관리자일 경우
        return 2;  // 본사
    }
}
