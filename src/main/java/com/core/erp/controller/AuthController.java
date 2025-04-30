package com.core.erp.controller;

import com.core.erp.dto.AuthRequest;
import com.core.erp.dto.AuthResponse;
import com.core.erp.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestParam String loginId, @RequestParam String loginPwd) {
        // AuthService를 호출해서 토큰 생성
        AuthResponse response = authService.login(loginId, loginPwd);

        // 토큰을 감싼 AuthResponse 객체를 응답으로 보냄
        return ResponseEntity.ok(response);
    }
}