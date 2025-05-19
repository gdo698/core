package com.core.erp.controller;

import com.core.erp.dto.statistics.*;
import com.core.erp.service.SalesStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/erp/statistics")
@RequiredArgsConstructor
public class SalesStatsController {

    private final SalesStatsService salesStatsService;

    // KPI 통계
    @GetMapping("/kpis")
    public KpiStatsDTO getKpis(
            @RequestParam Integer storeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return salesStatsService.getKpis(storeId, startDate, endDate);
    }

    // 시간대별 매출 통계
    @GetMapping("/sales/hourly")
    public List<HourlySalesDTO> getHourlySales(
            @RequestParam Integer storeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return salesStatsService.getHourlySales(storeId, startDate, endDate);
    }


    // 상품별 매출 순위
    @GetMapping("/sales/products")
    public List<ProductSalesDTO> getTopSalesProducts(@RequestParam Integer storeId,
                                                     @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return salesStatsService.getTopSalesProducts(storeId, date);
    }

    // 카테고리별 매출 비율 조회
    @GetMapping("/sales/categories")
    public List<CategorySalesDTO> getCategorySales(@RequestParam Integer storeId,
                                                   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return salesStatsService.getCategorySales(storeId, date);
    }

    // 발주 상품 순위 조회
    @GetMapping("/orders/products")
    public List<OrderProductDTO> getTopOrderProducts(
            @RequestParam Integer storeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return salesStatsService.getTopOrderProducts(storeId, date);
    }

}
