package com.core.erp.controller;

import com.core.erp.dto.CustomPrincipal;
import com.core.erp.dto.stock.StockFlowLogDTO;
import com.core.erp.dto.stock.StockFlowSearchCondition;
import com.core.erp.service.StockFlowService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/stock-flow")
@RequiredArgsConstructor
public class StockFlowController {

    private final StockFlowService stockFlowService;

    /**
     * 상품별 재고 흐름 로그 조회 (매장/본사 공통)
     */
    @GetMapping("/{productId}")
    public ResponseEntity<Page<StockFlowLogDTO>> getLogs(
            @AuthenticationPrincipal CustomPrincipal user,
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<StockFlowLogDTO> result = stockFlowService.getLogs(user, productId, page, size);
        return ResponseEntity.ok(result);
    }

    /**
     * 조건 기반 재고 흐름 로그 검색 (매장/본사 공통)
     */
    @PostMapping("/search")
    public ResponseEntity<Page<StockFlowLogDTO>> searchStockFlows(
            @RequestBody StockFlowSearchCondition condition,
            @AuthenticationPrincipal CustomPrincipal user
    ) {
        stockFlowService.bindUserStoreIfNeeded(condition, user);
        Page<StockFlowLogDTO> result = stockFlowService.searchFlows(condition);
        return ResponseEntity.ok(result);
    }
}
