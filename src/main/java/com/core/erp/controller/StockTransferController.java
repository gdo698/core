package com.core.erp.controller;

import com.core.erp.dto.stock.StockTransferRequestDTO;
import com.core.erp.service.StockTransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
public class StockTransferController {

    private final StockTransferService stockTransferService;

    /**
     * 재고 이동 요청 (창고 ↔ 매장)
     */
    @PostMapping("/transfer")
    public ResponseEntity<String> transferStock(@RequestBody StockTransferRequestDTO dto) {
        try {
            stockTransferService.transfer(dto);
            return ResponseEntity.ok("재고 이동 완료");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body("이동 실패: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("서버 오류: " + e.getMessage());
        }
    }
}
