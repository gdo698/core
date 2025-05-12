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
        try {
            List<Object[]> salesData = salesAnalysisRepository.findSalesByGender(
                    request.getStartDate(), request.getEndDate(), request.getStoreId());
            
            List<ChartDataDTO> chartData = new ArrayList<>();
            double totalSales = 0;
            int totalTransactions = 0;
            
            Map<Integer, String> genderMap = Map.of(
                    0, "남성",
                    1, "여성"
            );
            
            for (Object[] row : salesData) {
                try {
                    Integer genderCode;
                    if (row[0] == null) {
                        // NULL 값 처리
                        genderCode = -1; // 기타로 처리할 값
                    } else if (row[0] instanceof Number) {
                        genderCode = ((Number) row[0]).intValue();
                    } else {
                        // String이나 다른 타입으로 반환될 경우 기타로 처리
                        genderCode = -1;
                        log.warn("성별 데이터가 예상과 다른 타입으로 반환됨: {}, 타입: {}", row[0], row[0].getClass().getName());
                    }
                    
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
                } catch (Exception e) {
                    log.error("성별 데이터 처리 중 오류 발생: {}", e.getMessage(), e);
                }
            }
            
            // 데이터가 없는 경우에도 차트 데이터 생성
            if (chartData.isEmpty()) {
                for (Map.Entry<Integer, String> entry : genderMap.entrySet()) {
                    ChartDataDTO emptyDataPoint = ChartDataDTO.builder()
                            .label(entry.getValue())
                            .value(0.0)
                            .build();
                    emptyDataPoint.getAdditionalData().put("transactions", 0);
                    emptyDataPoint.getAdditionalData().put("averageTransaction", 0.0);
                    chartData.add(emptyDataPoint);
                }
                
                ChartDataDTO emptyOtherDataPoint = ChartDataDTO.builder()
                        .label("기타")
                        .value(0.0)
                        .build();
                emptyOtherDataPoint.getAdditionalData().put("transactions", 0);
                emptyOtherDataPoint.getAdditionalData().put("averageTransaction", 0.0);
                chartData.add(emptyOtherDataPoint);
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
        } catch (Exception e) {
            log.error("성별별 매출 분석 중 오류 발생: {}", e.getMessage(), e);
            
            // 오류 발생 시 빈 데이터 반환
            List<ChartDataDTO> emptyChartData = new ArrayList<>();
            emptyChartData.add(ChartDataDTO.builder().label("남성").value(0.0).build());
            emptyChartData.add(ChartDataDTO.builder().label("여성").value(0.0).build());
            emptyChartData.add(ChartDataDTO.builder().label("기타").value(0.0).build());
            
            SalesSummaryDTO emptySummary = SalesSummaryDTO.builder()
                    .totalSales(0)
                    .totalTransactions(0)
                    .averageTransaction(0)
                    .build();
            
            return SalesAnalysisResponseDTO.builder()
                    .chartData(emptyChartData)
                    .summary(emptySummary)
                    .build();
        }
    }

    /**
     * 날씨별 매출 분석 (기상청 ASOS 일자료 API 활용)
     */
    public SalesAnalysisResponseDTO analyzeSalesByWeather(SalesAnalysisRequestDTO request) {
        // 우선 날짜별 매출 데이터 가져오기
        List<Object[]> salesData = salesAnalysisRepository.findSalesByDateRange(
                request.getStartDate(), request.getEndDate(), request.getStoreId());
        
        // 날씨별로 매출 데이터 그룹화
        Map<String, List<Object[]>> salesByWeather = salesData.stream()
                .collect(Collectors.groupingBy(row -> {
                    // 날짜 문자열을 LocalDateTime으로 변환
                    String dateStr = row[0].toString();
                    LocalDateTime date = LocalDateTime.parse(dateStr + "T00:00:00");
                    
                    // 해당 날짜의 날씨 정보 가져오기 (ASOS API 사용)
                    // 서울 지점번호(108) 기준, 필요시 다른 지점 사용 가능
                    String weather = weatherService.getWeatherForDate(date, "108", null);
                    log.info("날짜: {}, 날씨: {}", dateStr, weather);
                    return weather;
                }));
        
        // 날씨별 매출 합계 계산
        List<ChartDataDTO> chartData = new ArrayList<>();
        double totalSales = 0;
        int totalTransactions = 0;
        int totalDays = salesData.size();
        
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
            
            // 추가 데이터 설정
            int daysCount = weatherSalesData.size();
            double avgDailySales = daysCount > 0 ? sales / daysCount : 0;
            
            dataPoint.getAdditionalData().put("transactions", transactions);
            dataPoint.getAdditionalData().put("averageTransaction", transactions > 0 ? sales / transactions : 0);
            dataPoint.getAdditionalData().put("days", daysCount);
            dataPoint.getAdditionalData().put("daysPercentage", totalDays > 0 ? Math.round((double)daysCount / totalDays * 1000) / 10.0 : 0);
            dataPoint.getAdditionalData().put("avgDailySales", avgDailySales);
            
            // 날씨별 평균 매출과 전체 평균 매출 비교
            double avgTotalDailySales = totalDays > 0 ? totalSales / totalDays : 0;
            double impactRate = avgTotalDailySales > 0 ? ((avgDailySales - avgTotalDailySales) / avgTotalDailySales) * 100 : 0;
            dataPoint.getAdditionalData().put("impactRate", Math.round(impactRate * 10) / 10.0); // 소수점 1자리까지
            
            chartData.add(dataPoint);
        }
        
        // 매출액 순으로 정렬
        chartData.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
        
        SalesSummaryDTO summary = SalesSummaryDTO.builder()
                .totalSales(totalSales)
                .totalTransactions(totalTransactions)
                .averageTransaction(totalTransactions > 0 ? totalSales / totalTransactions : 0)
                .totalDays(totalDays)
                .build();
        
        // 가장 매출이 높은/낮은 날씨 정보 추가
        if (!chartData.isEmpty()) {
            summary.getAdditionalData().put("bestWeather", chartData.get(0).getLabel());
            summary.getAdditionalData().put("worstWeather", chartData.get(chartData.size() - 1).getLabel());
        }
        
        return SalesAnalysisResponseDTO.builder()
                .chartData(chartData)
                .summary(summary)
                .build();
    }
} 