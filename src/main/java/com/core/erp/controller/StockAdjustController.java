package com.core.erp.controller;

import com.core.erp.dto.CustomPrincipal;
import com.core.erp.dto.StockAdjustDTO;
import com.core.erp.dto.StockAdjustLogDTO;
import com.core.erp.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
public class StockAdjustController {

    private final StockService stockService;

    @PatchMapping("/manual-adjust")
    public ResponseEntity<String> manualAdjustStock(
            @RequestBody StockAdjustDTO dto,
            @AuthenticationPrincipal CustomPrincipal user
    ) {
        stockService.adjustStock(dto, user);
        return ResponseEntity.ok("재고 수량이 수정되었습니다.");
    }

    @GetMapping("/adjust-log") // ✅ GET 로그 전체
    public ResponseEntity<Page<StockAdjustLogDTO>> getAdjustLogs(
            @AuthenticationPrincipal CustomPrincipal user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<StockAdjustLogDTO> result = stockService.getAdjustmentLogs(
                user.getStoreId(), user.getRole(), page, size);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/adjust-log/filter")
    public ResponseEntity<Page<StockAdjustLogDTO>> filterAdjustLogs(
            @AuthenticationPrincipal CustomPrincipal user,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(required = false) String adjustedBy,
            @RequestParam(required = false) String productName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<StockAdjustLogDTO> result = stockService.filterAdjustmentLogs(
                user.getStoreId(), user.getRole(), from, to, adjustedBy, productName, page, size);
        return ResponseEntity.ok(result);
    }
}
