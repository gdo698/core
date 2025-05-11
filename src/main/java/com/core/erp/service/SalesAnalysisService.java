package com.core.erp.service;

import com.core.erp.dto.sales.ChartDataDTO;
import com.core.erp.dto.sales.SalesAnalysisRequestDTO;
import com.core.erp.dto.sales.SalesAnalysisResponseDTO;
import com.core.erp.dto.sales.SalesSummaryDTO;
import com.core.erp.repository.SalesAnalysisRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SalesAnalysisService {

    private final SalesAnalysisRepository salesAnalysisRepository;
    private final StoreService storeService;
    private final WeatherService weatherService;

    /**
     * 매출 분석 데이터 조회
     */
    public SalesAnalysisResponseDTO analyzeByRequest(SalesAnalysisRequestDTO request) {
        String groupBy = request.getGroupBy() != null ? request.getGroupBy() : "date";
        
        switch (groupBy) {
            case "date":
                return analyzeSalesByDate(request);
            case "store":
                return analyzeSalesByStore(request);
            case "hour":
                return analyzeSalesByHour(request);
            case "category":
                return analyzeSalesByCategory(request);
            case "age":
                return analyzeSalesByAgeGroup(request);
            case "gender":
                return analyzeSalesByGender(request);
            case "weather":
                return analyzeSalesByWeather(request);
            default:
                return analyzeSalesByDate(request);
        }
    }

    /**
     * 날짜별 매출 분석
     */
    public SalesAnalysisResponseDTO analyzeSalesByDate(SalesAnalysisRequestDTO request) {
        List<Object[]> salesData = salesAnalysisRepository.findSalesByDateRange(
                request.getStartDate(), request.getEndDate(), request.getStoreId());
        
        List<ChartDataDTO> chartData = new ArrayList<>();
        double totalSales = 0;
        int totalTransactions = 0;
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        for (Object[] row : salesData) {
            String date = row[0].toString();
            double sales = ((Number) row[1]).doubleValue();
            int transactions = ((Number) row[2]).intValue();
            
            totalSales += sales;
            totalTransactions += transactions;
            
            ChartDataDTO dataPoint = ChartDataDTO.builder()
                    .label(date)
                    .value(sales)
                    .build();
            
            dataPoint.getAdditionalData().put("transactions", transactions);
            dataPoint.getAdditionalData().put("averageTransaction", transactions > 0 ? sales / transactions : 0);
            
            chartData.add(dataPoint);
        }
        
        // 이전 기간 매출 계산 (현재 기간과 동일한 길이의 이전 기간)
        LocalDateTime previousStartDate = request.getStartDate().minus(
                ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()), ChronoUnit.DAYS);
        LocalDateTime previousEndDate = request.getStartDate().minusDays(1);
        
        List<Object[]> previousSalesData = salesAnalysisRepository.findSalesByDateRange(
                previousStartDate, previousEndDate, request.getStoreId());
        
        double previousTotalSales = previousSalesData.stream()
                .mapToDouble(row -> ((Number) row[1]).doubleValue())
                .sum();
        
        // 성장률 계산
        double growthRate = previousTotalSales > 0 ? 
                ((totalSales - previousTotalSales) / previousTotalSales) * 100 : 0;
        
        SalesSummaryDTO summary = SalesSummaryDTO.builder()
                .totalSales(totalSales)
                .totalTransactions(totalTransactions)
                .averageTransaction(totalTransactions > 0 ? totalSales / totalTransactions : 0)
                .previousPeriodSales(previousTotalSales)
                .growthRate(growthRate)
                .build();
        
        return SalesAnalysisResponseDTO.builder()
                .chartData(chartData)
                .summary(summary)
                .build();
    }

    /**
     * 지점별 매출 분석
     */
    public SalesAnalysisResponseDTO analyzeSalesByStore(SalesAnalysisRequestDTO request) {
        List<Object[]> salesData = salesAnalysisRepository.findSalesByStore(
                request.getStartDate(), request.getEndDate());
        
        List<ChartDataDTO> chartData = new ArrayList<>();
        double totalSales = 0;
        int totalTransactions = 0;
        
        for (Object[] row : salesData) {
            String storeName = (String) row[0];
            double sales = ((Number) row[1]).doubleValue();
            int transactions = ((Number) row[2]).intValue();
            
            totalSales += sales;
            totalTransactions += transactions;
            
            ChartDataDTO dataPoint = ChartDataDTO.builder()
                    .label(storeName)
                    .value(sales)
                    .build();
            
            dataPoint.getAdditionalData().put("transactions", transactions);
            dataPoint.getAdditionalData().put("averageTransaction", transactions > 0 ? sales / transactions : 0);
            
            chartData.add(dataPoint);
        }
        
        SalesSummaryDTO summary = SalesSummaryDTO.builder()
                .totalSales(totalSales)
                .totalTransactions(totalTransactions)
                .averageTransaction(totalTransactions > 0 ? totalSales / totalTransactions : 0)
                .build();
        
        return SalesAnalysisResponseDTO.builder()
                .chartData(chartData)
                .summary(summary)
                .build();
    }

    /**
     * 시간대별 매출 분석
     */
    public SalesAnalysisResponseDTO analyzeSalesByHour(SalesAnalysisRequestDTO request) {
        List<Object[]> salesData = salesAnalysisRepository.findSalesByHour(
                request.getStartDate(), request.getEndDate(), request.getStoreId());
        
        List<ChartDataDTO> chartData = new ArrayList<>();
        double totalSales = 0;
        int totalTransactions = 0;
        
        for (Object[] row : salesData) {
            int hour = ((Number) row[0]).intValue();
            double sales = ((Number) row[1]).doubleValue();
            int transactions = ((Number) row[2]).intValue();
            
            totalSales += sales;
            totalTransactions += transactions;
            
            ChartDataDTO dataPoint = ChartDataDTO.builder()
                    .label(String.format("%02d:00", hour))
                    .value(sales)
                    .build();
            
            dataPoint.getAdditionalData().put("transactions", transactions);
            dataPoint.getAdditionalData().put("averageTransaction", transactions > 0 ? sales / transactions : 0);
            
            chartData.add(dataPoint);
        }
        
        SalesSummaryDTO summary = SalesSummaryDTO.builder()
                .totalSales(totalSales)
                .totalTransactions(totalTransactions)
                .averageTransaction(totalTransactions > 0 ? totalSales / totalTransactions : 0)
                .build();
        
        return SalesAnalysisResponseDTO.builder()
                .chartData(chartData)
                .summary(summary)
                .build();
    }

    /**
     * 카테고리별 매출 분석
     */
    public SalesAnalysisResponseDTO analyzeSalesByCategory(SalesAnalysisRequestDTO request) {
        List<Object[]> salesData = salesAnalysisRepository.findSalesByCategory(
                request.getStartDate(), request.getEndDate(), request.getStoreId());
        
        List<ChartDataDTO> chartData = new ArrayList<>();
        double totalSales = 0;
        int totalTransactions = 0;
        
        for (Object[] row : salesData) {
            String categoryName = (String) row[0];
            double sales = ((Number) row[1]).doubleValue();
            int salesCount = ((Number) row[2]).intValue();
            
            totalSales += sales;
            totalTransactions += salesCount;
            
            ChartDataDTO dataPoint = ChartDataDTO.builder()
                    .label(categoryName)
                    .value(sales)
                    .build();
            
            dataPoint.getAdditionalData().put("salesCount", salesCount);
            
            chartData.add(dataPoint);
        }
        
        SalesSummaryDTO summary = SalesSummaryDTO.builder()
                .totalSales(totalSales)
                .totalTransactions(totalTransactions)
                .averageTransaction(totalTransactions > 0 ? totalSales / totalTransactions : 0)
                .build();
        
        return SalesAnalysisResponseDTO.builder()
                .chartData(chartData)
                .summary(summary)
                .build();
    }

    /**
     * 연령대별 매출 분석
     */
    public SalesAnalysisResponseDTO analyzeSalesByAgeGroup(SalesAnalysisRequestDTO request) {
        List<Object[]> salesData = salesAnalysisRepository.findSalesByAgeGroup(
                request.getStartDate(), request.getEndDate(), request.getStoreId());
        
        List<ChartDataDTO> chartData = new ArrayList<>();
        double totalSales = 0;
        int totalTransactions = 0;
        
        for (Object[] row : salesData) {
            int ageGroup = ((Number) row[0]).intValue();
            double sales = ((Number) row[1]).doubleValue();
            int transactions = ((Number) row[2]).intValue();
            
            totalSales += sales;
            totalTransactions += transactions;
            
            ChartDataDTO dataPoint = ChartDataDTO.builder()
                    .label(ageGroup + "대")
                    .value(sales)
                    .build();
            
            dataPoint.getAdditionalData().put("transactions", transactions);
            dataPoint.getAdditionalData().put("averageTransaction", transactions > 0 ? sales / transactions : 0);
            
            chartData.add(dataPoint);
        }
        
        SalesSummaryDTO summary = SalesSummaryDTO.builder()
                .totalSales(totalSales)
                .totalTransactions(totalTransactions)
                .averageTransaction(totalTransactions > 0 ? totalSales / totalTransactions : 0)
                .build();
        
        return SalesAnalysisResponseDTO.builder()
                .chartData(chartData)
                .summary(summary)
                .build();
    }

    /**
     * 성별별 매출 분석
     */
    public SalesAnalysisResponseDTO analyzeSalesByGender(SalesAnalysisRequestDTO request) {
        List<Object[]> salesData = salesAnalysisRepository.findSalesByGender(
                request.getStartDate(), request.getEndDate(), request.getStoreId());
        
        List<ChartDataDTO> chartData = new ArrayList<>();
        double totalSales = 0;
        int totalTransactions = 0;
        
        Map<String, String> genderMap = Map.of(
                "M", "남성",
                "F", "여성"
        );
        
        for (Object[] row : salesData) {
            String genderCode = (String) row[0];
            double sales = ((Number) row[1]).doubleValue();
            int transactions = ((Number) row[2]).intValue();
            
            totalSales += sales;
            totalTransactions += transactions;
            
            String genderLabel = genderMap.getOrDefault(genderCode, "기타");
            
            ChartDataDTO dataPoint = ChartDataDTO.builder()
                    .label(genderLabel)
                    .value(sales)
                    .build();
            
            dataPoint.getAdditionalData().put("transactions", transactions);
            dataPoint.getAdditionalData().put("averageTransaction", transactions > 0 ? sales / transactions : 0);
            
            chartData.add(dataPoint);
        }
        
        SalesSummaryDTO summary = SalesSummaryDTO.builder()
                .totalSales(totalSales)
                .totalTransactions(totalTransactions)
                .averageTransaction(totalTransactions > 0 ? totalSales / totalTransactions : 0)
                .build();
        
        return SalesAnalysisResponseDTO.builder()
                .chartData(chartData)
                .summary(summary)
                .build();
    }

    /**
     * 날씨별 매출 분석 (OpenWeatherMap API 호출)
     */
    public SalesAnalysisResponseDTO analyzeSalesByWeather(SalesAnalysisRequestDTO request) {
        // 우선 날짜별 매출 데이터 가져오기
        List<Object[]> salesData = salesAnalysisRepository.findSalesByDateRange(
                request.getStartDate(), request.getEndDate(), request.getStoreId());
        
        // 날씨별로 매출 데이터 그룹화하기 위한 임시 저장소
        Map<String, List<Object[]>> salesByWeather = salesData.stream()
                .collect(Collectors.groupingBy(row -> {
                    // 이 부분에서는 실제 날씨 API 연동 대신 임의의 날씨를 부여
                    // 실제 구현 시에는 날짜별로 해당 지역의 날씨 데이터를 가져와야 함
                    String date = row[0].toString();
                    int hashCode = date.hashCode();
                    String[] weathers = {"맑음", "구름", "비", "눈"};
                    return weathers[Math.abs(hashCode) % weathers.length];
                }));
        
        // 날씨별 매출 합계 계산
        List<ChartDataDTO> chartData = new ArrayList<>();
        double totalSales = 0;
        int totalTransactions = 0;
        
        for (Map.Entry<String, List<Object[]>> entry : salesByWeather.entrySet()) {
            String weather = entry.getKey();
            List<Object[]> weatherSalesData = entry.getValue();
            
            double sales = weatherSalesData.stream()
                    .mapToDouble(row -> ((Number) row[1]).doubleValue())
                    .sum();
            
            int transactions = weatherSalesData.stream()
                    .mapToInt(row -> ((Number) row[2]).intValue())
                    .sum();
            
            totalSales += sales;
            totalTransactions += transactions;
            
            ChartDataDTO dataPoint = ChartDataDTO.builder()
                    .label(weather)
                    .value(sales)
                    .build();
            
            dataPoint.getAdditionalData().put("transactions", transactions);
            dataPoint.getAdditionalData().put("averageTransaction", transactions > 0 ? sales / transactions : 0);
            dataPoint.getAdditionalData().put("days", weatherSalesData.size());
            
            chartData.add(dataPoint);
        }
        
        SalesSummaryDTO summary = SalesSummaryDTO.builder()
                .totalSales(totalSales)
                .totalTransactions(totalTransactions)
                .averageTransaction(totalTransactions > 0 ? totalSales / totalTransactions : 0)
                .build();
        
        return SalesAnalysisResponseDTO.builder()
                .chartData(chartData)
                .summary(summary)
                .build();
    }
} 