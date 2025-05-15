package com.core.pos.controller;

import com.core.erp.dto.CustomPrincipal;
import com.core.pos.dto.*;
import com.core.pos.service.PosService;
import com.core.pos.service.SettlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/pos")
@RequiredArgsConstructor
public class PosController {

    private final PosService posService;
    private final SettlementService settlementService;

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

    // 환불 처리 API
    @PostMapping("/refund/{transactionId}")
    public ResponseEntity<String> refundTransaction(
            @PathVariable Integer transactionId,
            @RequestParam String refundReason
    ) {
        try {
            posService.processRefund(transactionId, refundReason);
            return ResponseEntity.ok("환불 처리 완료");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body("환불 실패: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("서버 오류: " + e.getMessage());
        }
    }

    // 폐기 등록 API
    @PostMapping("/disposals")
    public ResponseEntity<String> registerDisposal(@RequestBody DisposalRequestDTO dto, Authentication authentication) {
        try {
            // 로그인된 사용자 정보로 등록자 이름 처리
            CustomPrincipal principal = (CustomPrincipal) authentication.getPrincipal();
            String loginId = principal.getLoginId();

            posService.saveDisposal(dto, loginId);

            return ResponseEntity.ok("폐기 등록 완료");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("폐기 등록 실패: " + e.getMessage());
        }
    }

    // 일별 정산 API
    @PostMapping("/settlement/daily")
    public ResponseEntity<?> calculateDailySettlement(@RequestBody SettlementRequestDTO dto) {
        settlementService.calculateDailySettlement(dto);
        return ResponseEntity.ok("정산 완료");
    }

    // 월별 정산 API
    @PostMapping("/settlement/monthly")
    public ResponseEntity<?> calculateMonthlySettlement(@RequestBody SettlementRequestDTO dto) {
        settlementService.calculateMonthlySettlement(dto);
        return ResponseEntity.ok("월별 정산 완료");
    }

    // 연별 정산 API
    @PostMapping("/settlement/yearly")
    public ResponseEntity<?> calculateYearly(@RequestBody SettlementRequestDTO dto) {
        settlementService.calculateYearlySettlement(dto);
        return ResponseEntity.ok("연별 정산 완료");
    }

    // 영수증
    @GetMapping("/receipt/{transactionId}")
    public ResponseEntity<SalesHistoryDTO> getReceipt(@PathVariable Integer transactionId) {
        return ResponseEntity.ok(posService.getReceiptByTransactionId(transactionId));
    }


}