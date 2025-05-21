package com.core.erp.controller;

import com.core.erp.dto.statistics.*;
import com.core.erp.service.SalesStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/erp/statistics")
@RequiredArgsConstructor
public class SalesStatsController {

    private final SalesStatsService salesStatsService;

    //  1. KPI 통계
    @GetMapping("/kpis")
    public KpiStatsDTO getKpis(
            @RequestParam Integer storeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return salesStatsService.getKpis(storeId, startDate, endDate);
    }

    //  2. 시간대별 매출 통계
    @GetMapping("/sales/hourly")
    public List<HourlySalesDTO> getHourlySales(
            @RequestParam Integer storeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return salesStatsService.getHourlySales(storeId, startDate, endDate);
    }

    //  3. 상품별 매출 통계 (수량/금액 기준 선택 + 카테고리 필터 + 날짜 범위)
    @GetMapping("/sales/products")
    public List<ProductSalesDTO> getTopSalesProducts(
            @RequestParam Integer storeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) List<Long> categoryIds,
            @RequestParam(defaultValue = "quantity") String sortBy
    ) {
        return salesStatsService.getTopSalesProducts(storeId, startDate, endDate, categoryIds, sortBy);
    }

    //  4. 카테고리별 매출 비율 (날짜 범위 + 카테고리 필터)
    @GetMapping("/sales/categories")
    public List<CategorySalesDTO> getCategorySales(
            @RequestParam Integer storeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) List<Long> categoryIds
    ) {
        return salesStatsService.getCategorySales(storeId, startDate, endDate, categoryIds);
    }

    // 5. 발주 상품 순위 (날짜 범위 기반으로 확장)
    @GetMapping("/orders/products")
    public List<OrderProductDTO> getTopOrderProducts(
            @RequestParam Integer storeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return salesStatsService.getTopOrderProducts(storeId, startDate, endDate);
    }
}

