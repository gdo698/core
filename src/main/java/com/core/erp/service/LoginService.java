package com.core.erp.service;

import com.core.erp.domain.EmailTokenEntity;
import com.core.erp.domain.EmployeeEntity;
import com.core.erp.dto.LoginDTO;
import com.core.erp.repository.EmailTokenRepository;
import com.core.erp.repository.MemberRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginService {

    private final MemberRepository memberRepository;
    private final EmailTokenRepository emailTokenRepository;
    private final HttpSession session;

    public ResultStatus login(LoginDTO loginDTO) {
        log.info("로그인 요청 데이터: loginId={}, loginPwd={}", loginDTO.getLoginId(), loginDTO.getLoginPwd());

        EmployeeEntity employee = memberRepository.findByLoginId(loginDTO.getLoginId());

        if(employee == null) {
            log.warn("아이디를 찾을 수 없음: {}", loginDTO.getLoginId());
            return ResultStatus.ID_NOT_FOUND;
        }

        if(!employee.getLoginPwd().equals(loginDTO.getLoginPwd())) {
            log.warn("비밀번호 불일치: loginId={}", loginDTO.getLoginId());
            return ResultStatus.PASSWORD_MISMATCH;
        }

        EmailTokenEntity emailToken = emailTokenRepository.findLatestToken(employee.getEmpId());
        boolean isTokenValid = (emailToken != null && emailToken.isEtokenUsed());
        boolean isEmailAuth = (employee.getEmailAuth() != null && employee.getEmailAuth() == 1);

        if (!isTokenValid && !isEmailAuth) {
            log.warn("이메일 인증 미완료: loginId={}", loginDTO.getLoginId());
            return ResultStatus.EMAIL_NOT_VERIFIED;
        }

        session.setAttribute("empId", employee.getEmpId());
        session.setAttribute("empName", employee.getEmpName());
        session.setAttribute("storeId", employee.getStore() != null ? employee.getStore().getStoreId() : null);
        session.setAttribute("departId", employee.getDepartment() != null ? employee.getDepartment().getDeptId() : null);
        session.setAttribute("workType", employee.getWorkType());
        session.setAttribute("loginEmployee", employee);

        log.info("로그인 성공: loginId={}", loginDTO.getLoginId());
        return ResultStatus.SUCCESS;
    }


    public void logout() {
        session.invalidate();
    }
}
