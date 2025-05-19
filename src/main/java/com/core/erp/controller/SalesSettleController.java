package com.core.erp.controller;

import com.core.erp.domain.SalesSettleEntity.SettlementType;

import com.core.erp.dto.sales.SalesSettleDTO;
import com.core.erp.service.SalesSettleService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/erp/settlement")
@RequiredArgsConstructor
public class SalesSettleController {

    private final SalesSettleService salesSettleService;

    // 점주 ERP – 정산 이력 조회 API
    @GetMapping("/list")
    public List<SalesSettleDTO> getSettlements(
            @RequestParam Integer storeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String type
    ) {
        // type이 null이거나 "ALL"이면 DAILY + SHIFT 전체 조회
        if (type == null || type.trim().isEmpty() || type.equalsIgnoreCase("ALL")) {
            return salesSettleService.getSettlements(storeId, startDate, endDate, null);
        }

        // 나머지는 enum으로 변환해서 전달
        try {
            SettlementType enumType = SettlementType.valueOf(type.toUpperCase());
            return salesSettleService.getSettlements(storeId, startDate, endDate, enumType);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("잘못된 정산 유형입니다: " + type);
        }
    }
}