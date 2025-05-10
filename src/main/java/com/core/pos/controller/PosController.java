package com.core.pos.controller;

import com.core.erp.dto.CustomPrincipal;
import com.core.pos.dto.SaleRequestDTO;
import com.core.pos.dto.SalesHistoryDTO;
import com.core.pos.service.PosService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pos")
@RequiredArgsConstructor
public class PosController {

    private final PosService posService;

    // 결제 저장 API
    @PostMapping("/pay")
    public ResponseEntity<?> savePayment(@RequestBody SaleRequestDTO dto, Authentication authentication) {

        // CustomPrincipal로 캐스팅해서 loginId 직접 추출
        CustomPrincipal principal = (CustomPrincipal) authentication.getPrincipal();
        String loginId = principal.getLoginId();

        posService.saveTransactionWithDetails(dto, loginId);

        return ResponseEntity.ok("결제 저장 완료");
    }

    // 거래내역 조회 API
    @GetMapping("/transactions")
    public ResponseEntity<List<SalesHistoryDTO>> getTransactions(@RequestParam("storeId") Integer storeId) {
        List<SalesHistoryDTO> historyList = posService.getTransactionHistoryByStore(storeId);
        return ResponseEntity.ok(historyList);
    }


}
