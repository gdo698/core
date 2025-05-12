package com.core.erp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 대시보드 요약 정보 DTO
 * - 점포 통계 (전체 점포수, 신규 점포수)
 * - 매출 통계 (당일 매출, 당월 매출)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryDTO {
    // 점포 통계
    private int totalStores;           // 전체 점포수
    private int newStores;             // 이번달 신규 점포수
    private double totalStoresGrowth;  // 전체 점포수 증감률 (전월 대비)
    private double newStoresGrowth;    // 신규 점포수 증감률 (전월 대비)
    
    // 매출 통계
    private long dailySales;          // 당일 매출 현황
    private double dailySalesGrowth;  // 당일 매출 증감률 (전일 대비)
    private long monthlySales;        // 당월 매출 현황
    private double monthlySalesGrowth; // 당월 매출 증감률 (전월 대비)
} 