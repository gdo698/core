package com.core.erp.controller;

import com.core.erp.domain.EmployeeEntity;
import com.core.erp.dto.LoginDTO;
import com.core.erp.service.LoginService;
import com.core.erp.service.ResultStatus;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    private final LoginService loginService;
    private final HttpSession session;

    @GetMapping("/login")
    public String loginForm() {
        return "auth/login";
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginDTO loginDTO) {

        log.info("로그인 요청 데이터: loginId={}, loginPwd={}", loginDTO.getLoginId(), loginDTO.getLoginPwd());

        // 로그인 서비스 호출하여 결과 상태 확인
        ResultStatus result = loginService.login(loginDTO);

        // 로그인 성공
        if (result == ResultStatus.SUCCESS) {
            EmployeeEntity employee = (EmployeeEntity) session.getAttribute("loginEmployee");

            Map<String, Object> response = new HashMap<>();
            response.put("message", "로그인 성공");
            response.put("workType", employee.getWorkType());
            return ResponseEntity.ok(response);
        }

        // 로그인 실패: 아이디, 비밀번호 오류
        if (result == ResultStatus.ID_NOT_FOUND) {
            return ResponseEntity.status(400).body("아이디를 찾을 수 없습니다.");
        } else if (result == ResultStatus.PASSWORD_MISMATCH) {
            return ResponseEntity.status(400).body("비밀번호가 일치하지 않습니다.");
        } else if (result == ResultStatus.EMAIL_NOT_VERIFIED) {
            return ResponseEntity.status(400).body("이메일 인증이 완료되지 않았습니다.");
        }

        // 그 외의 예외가 있을 경우
        return ResponseEntity.status(500).body("알 수 없는 오류가 발생했습니다.");
    }

    @GetMapping("/logout")
    public String logout() {
        loginService.logout();
        return "redirect:/auth/login";
    }
}
