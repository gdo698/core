package com.core.pos.controller;

import com.core.erp.dto.CustomPrincipal;
import com.core.pos.dto.SaleRequestDTO;
import com.core.pos.service.PosService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pos")
@RequiredArgsConstructor
public class PosController {

//    @GetMapping("/test")
//    public ResponseEntity<?>testAuth(Authentication autentication) {
//        String username = autentication.getName();
//        return ResponseEntity.ok("POS 인증 성공! 로그인한 사용자 " + username);
//    }

    private final PosService posService;

    @PostMapping("/pay")
    public ResponseEntity<?> savePayment(@RequestBody SaleRequestDTO dto, Authentication authentication) {

        // CustomPrincipal로 캐스팅해서 loginId 직접 추출
        CustomPrincipal principal = (CustomPrincipal) authentication.getPrincipal();
        String loginId = principal.getLoginId();

        posService.saveTransactionWithDetails(dto, loginId);

        return ResponseEntity.ok("결제 저장 완료");
    }


}
