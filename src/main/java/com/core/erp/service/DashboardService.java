package com.core.erp.service;

import com.core.erp.dto.DashboardSummaryDTO;
import com.core.erp.repository.SalesTransactionRepository;
import com.core.erp.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final StoreRepository storeRepository;
    private final SalesTransactionRepository salesTransactionRepository;

    /**
     * 점포 통계 조회
     * - 전체 점포수
     * - 이번달 신규 점포수
     * @return 점포 통계 정보
     */
    public Map<String, Object> getStoreStatistics() {
        // 현재 날짜 정보
        LocalDate today = LocalDate.now();
        YearMonth currentMonth = YearMonth.from(today);
        YearMonth lastMonth = currentMonth.minusMonths(1);
        
        // 전체 점포수 (영업중인 점포만)
        int totalStores = storeRepository.countByStoreStatus(1);
        int lastMonthTotalStores = storeRepository.countStoresByCreatedAtBefore(lastMonth.atEndOfMonth().atTime(23, 59, 59));
        
        // 이번달 신규 점포수
        LocalDateTime monthStart = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime monthEnd = currentMonth.atEndOfMonth().atTime(23, 59, 59);
        int newStores = storeRepository.countByStoreCreatedAtBetween(monthStart, monthEnd);
        
        // 저번달 신규 점포수
        LocalDateTime lastMonthStart = lastMonth.atDay(1).atStartOfDay();
        LocalDateTime lastMonthEnd = lastMonth.atEndOfMonth().atTime(23, 59, 59);
        int lastMonthNewStores = storeRepository.countByStoreCreatedAtBetween(lastMonthStart, lastMonthEnd);
        
        // 증감률 계산
        double totalStoresGrowth = lastMonthTotalStores > 0 
            ? ((double)(totalStores - lastMonthTotalStores) / lastMonthTotalStores) * 100 
            : 0;
        
        double newStoresGrowth = lastMonthNewStores > 0 
            ? ((double)(newStores - lastMonthNewStores) / lastMonthNewStores) * 100 
            : 0;
        
        return Map.of(
            "totalStores", totalStores,
            "newStores", newStores,
            "totalStoresGrowth", Math.round(totalStoresGrowth * 10) / 10.0,
            "newStoresGrowth", Math.round(newStoresGrowth * 10) / 10.0
        );
    }
    
    /**
     * 당일 매출 현황 조회
     * @return 당일 매출 정보
     */
    public Map<String, Object> getDailySales() {
        // 오늘 날짜와 어제 날짜
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDate.now().atTime(23, 59, 59);
        LocalDateTime yesterdayStart = LocalDate.now().minusDays(1).atStartOfDay();
        LocalDateTime yesterdayEnd = LocalDate.now().minusDays(1).atTime(23, 59, 59);
        
        // 당일 매출 합계 (transaction_status가 0인 경우만 - 완료 상태)
        Long dailySales = salesTransactionRepository.sumFinalAmountByPaidAtBetweenAndTransactionStatus(
            todayStart, todayEnd, 0
        );
        
        // null 체크 추가
        if (dailySales == null) {
            dailySales = 0L;
        }
        
        // 어제 매출 합계
        Long yesterdaySales = salesTransactionRepository.sumFinalAmountByPaidAtBetweenAndTransactionStatus(
            yesterdayStart, yesterdayEnd, 0
        );
        
        // null 체크 추가
        if (yesterdaySales == null) {
            yesterdaySales = 0L;
        }
        
        // 증감률 계산
        double dailySalesGrowth = yesterdaySales > 0 
            ? ((double)(dailySales - yesterdaySales) / yesterdaySales) * 100 
            : 0;
        
        return Map.of(
            "dailySales", dailySales,
            "dailySalesGrowth", Math.round(dailySalesGrowth * 10) / 10.0
        );
    }
    
    /**
     * 당월 매출 현황 조회
     * @return 당월 매출 정보
     */
    public Map<String, Object> getMonthlySales() {
        // 이번달과 저번달 기간
        YearMonth currentMonth = YearMonth.now();
        YearMonth lastMonth = currentMonth.minusMonths(1);
        
        LocalDateTime currentMonthStart = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime currentMonthEnd = currentMonth.atEndOfMonth().atTime(23, 59, 59);
        LocalDateTime lastMonthStart = lastMonth.atDay(1).atStartOfDay();
        LocalDateTime lastMonthEnd = lastMonth.atEndOfMonth().atTime(23, 59, 59);
        
        // 당월 매출 합계
        Long monthlySales = salesTransactionRepository.sumFinalAmountByPaidAtBetweenAndTransactionStatus(
            currentMonthStart, currentMonthEnd, 0
        );
        
        // null 체크 추가
        if (monthlySales == null) {
            monthlySales = 0L;
        }
        
        // 전월 매출 합계
        Long lastMonthSales = salesTransactionRepository.sumFinalAmountByPaidAtBetweenAndTransactionStatus(
            lastMonthStart, lastMonthEnd, 0
        );
        
        // null 체크 추가
        if (lastMonthSales == null) {
            lastMonthSales = 0L;
        }
        
        // 증감률 계산
        double monthlySalesGrowth = lastMonthSales > 0 
            ? ((double)(monthlySales - lastMonthSales) / lastMonthSales) * 100 
            : 0;
        
        return Map.of(
            "monthlySales", monthlySales,
            "monthlySalesGrowth", Math.round(monthlySalesGrowth * 10) / 10.0
        );
    }
    
    /**
     * 대시보드 요약 정보 조회 (모든 정보 한번에)
     * @return 대시보드 요약 정보
     */
    public DashboardSummaryDTO getDashboardSummary() {
        // 점포 통계 조회
        Map<String, Object> storeStats = getStoreStatistics();
        
        // 매출 통계 조회
        Map<String, Object> dailySales = getDailySales();
        Map<String, Object> monthlySales = getMonthlySales();
        
        // DTO로 합치기
        return DashboardSummaryDTO.builder()
            .totalStores((int) storeStats.get("totalStores"))
            .newStores((int) storeStats.get("newStores"))
            .totalStoresGrowth((double) storeStats.get("totalStoresGrowth"))
            .newStoresGrowth((double) storeStats.get("newStoresGrowth"))
            .dailySales((long) dailySales.get("dailySales"))
            .dailySalesGrowth((double) dailySales.get("dailySalesGrowth"))
            .monthlySales((long) monthlySales.get("monthlySales"))
            .monthlySalesGrowth((double) monthlySales.get("monthlySalesGrowth"))
            .build();
    }
} 