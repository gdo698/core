package com.core.erp.controller;

import com.core.erp.dto.DashboardSummaryDTO;
import com.core.erp.dto.WidgetData;
import com.core.erp.service.DashboardService;
import com.core.erp.service.WidgetDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final WidgetDashboardService widgetDashboardService;

    /**
     * 점포 통계 조회 (전체 점포수, 신규 점포수)
     * @return 점포 통계 정보
     */
    @GetMapping("/store-statistics")
    public ResponseEntity<?> getStoreStatistics() {
        return ResponseEntity.ok(dashboardService.getStoreStatistics());
    }

    /**
     * 당일 매출 현황 조회 (전체 점포 기준)
     * @return 당일 매출 정보
     */
    @GetMapping("/daily-sales")
    public ResponseEntity<?> getDailySales() {
        return ResponseEntity.ok(dashboardService.getDailySales());
    }

    /**
     * 당월 매출 현황 조회 (전체 점포 기준)
     * @return 당월 매출 정보
     */
    @GetMapping("/monthly-sales")
    public ResponseEntity<?> getMonthlySales() {
        return ResponseEntity.ok(dashboardService.getMonthlySales());
    }

    /**
     * 대시보드 요약 정보 조회 (모든 정보 한번에)
     * @return 대시보드 요약 정보
     */
    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryDTO> getDashboardSummary() {
        return ResponseEntity.ok(dashboardService.getDashboardSummary());
    }
    
    /**
     * 직원별 대시보드 위젯 목록 조회
     * @param empId 직원 ID
     * @return 위젯 목록
     */
    @GetMapping("/widgets/{empId}")
    public List<WidgetData> getDashboardWidgets(@PathVariable Long empId) {
        return widgetDashboardService.getWidgetsForEmployee(empId);
    }

    /**
     * 특정 위젯 데이터 조회
     * @param widgetCode 위젯 코드
     * @param storeId 점포 ID
     * @return 위젯 데이터
     */
    @GetMapping("/widget/{widgetCode}")
    public WidgetData getWidgetData(
            @PathVariable String widgetCode,
            @RequestParam Long storeId) {
        return widgetDashboardService.getWidgetData(widgetCode, storeId);
    }
} 