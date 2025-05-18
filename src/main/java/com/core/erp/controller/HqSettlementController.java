package com.core.erp.controller;
import com.core.erp.service.HqSettlementService;
import com.core.pos.dto.SettlementDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hq/settlements")
@RequiredArgsConstructor
@Slf4j
public class HqSettlementController {

    private final HqSettlementService hqSettlementService;

    @PostMapping
    public ResponseEntity<String> receiveSettlement(@RequestBody SettlementDTO dto) {
        log.info("📥 [본사 수신] 정산 도착 - storeId: {}, type: {}, 금액: {}원",
                dto.getStoreId(), dto.getSettlementType(), dto.getFinalAmount());

        try {
            hqSettlementService.saveSettlement(dto);
            return ResponseEntity.ok("정산 수신 및 저장 완료");
        } catch (Exception e) {
            log.error("❌ 정산 저장 중 오류 발생", e);
            return ResponseEntity.internalServerError().body("정산 저장 실패: " + e.getMessage());
        }
    }
}
