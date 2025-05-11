package com.core.erp.controller;

import com.core.erp.dto.sales.SalesAnalysisRequestDTO;
import com.core.erp.dto.sales.SalesAnalysisResponseDTO;
import com.core.erp.service.SalesAnalysisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Slf4j
@RestController
@RequestMapping("/api/sales/analysis")
@RequiredArgsConstructor
public class SalesAnalysisController {

    private final SalesAnalysisService salesAnalysisService;

    @GetMapping("/overview")
    public ResponseEntity<SalesAnalysisResponseDTO> getOverview(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) Integer storeId,
            @RequestParam(required = false) String dateUnit) {

        // 기본값 설정: 시작일은 현재 날짜에서 30일 전, 종료일은 현재 날짜
        if (startDate == null) {
            startDate = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS).minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }

        SalesAnalysisRequestDTO request = SalesAnalysisRequestDTO.builder()
                .startDate(startDate)
                .endDate(endDate)
                .storeId(storeId)
                .dateUnit(dateUnit != null ? dateUnit : "day")
                .build();

        return ResponseEntity.ok(salesAnalysisService.analyzeByRequest(request));
    }

    @GetMapping("/by-store")
    @PreAuthorize("hasAnyRole('ROLE_HQ_BR', 'ROLE_HQ_BR_M', 'ROLE_MASTER')")
    public ResponseEntity<SalesAnalysisResponseDTO> getByStore(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        if (startDate == null) {
            startDate = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS).minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }

        SalesAnalysisRequestDTO request = SalesAnalysisRequestDTO.builder()
                .startDate(startDate)
                .endDate(endDate)
                .groupBy("store")
                .build();

        return ResponseEntity.ok(salesAnalysisService.analyzeSalesByStore(request));
    }

    @GetMapping("/by-date")
    public ResponseEntity<SalesAnalysisResponseDTO> getByDate(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) Integer storeId,
            @RequestParam(required = false) String dateUnit) {

        if (startDate == null) {
            startDate = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS).minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }

        SalesAnalysisRequestDTO request = SalesAnalysisRequestDTO.builder()
                .startDate(startDate)
                .endDate(endDate)
                .storeId(storeId)
                .groupBy("date")
                .dateUnit(dateUnit != null ? dateUnit : "day")
                .build();

        return ResponseEntity.ok(salesAnalysisService.analyzeSalesByDate(request));
    }

    @GetMapping("/by-time")
    public ResponseEntity<SalesAnalysisResponseDTO> getByTime(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) Integer storeId) {

        if (startDate == null) {
            startDate = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS).minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }

        SalesAnalysisRequestDTO request = SalesAnalysisRequestDTO.builder()
                .startDate(startDate)
                .endDate(endDate)
                .storeId(storeId)
                .groupBy("hour")
                .build();

        return ResponseEntity.ok(salesAnalysisService.analyzeSalesByHour(request));
    }

    @GetMapping("/by-demographic")
    public ResponseEntity<SalesAnalysisResponseDTO> getByDemographic(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) Integer storeId,
            @RequestParam(defaultValue = "age") String type) {

        if (startDate == null) {
            startDate = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS).minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }

        SalesAnalysisRequestDTO request = SalesAnalysisRequestDTO.builder()
                .startDate(startDate)
                .endDate(endDate)
                .storeId(storeId)
                .groupBy(type) // "age" 또는 "gender"
                .build();

        if ("gender".equals(type)) {
            return ResponseEntity.ok(salesAnalysisService.analyzeSalesByGender(request));
        } else {
            return ResponseEntity.ok(salesAnalysisService.analyzeSalesByAgeGroup(request));
        }
    }

    @GetMapping("/by-category")
    public ResponseEntity<SalesAnalysisResponseDTO> getByCategory(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) Integer storeId) {

        if (startDate == null) {
            startDate = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS).minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }

        SalesAnalysisRequestDTO request = SalesAnalysisRequestDTO.builder()
                .startDate(startDate)
                .endDate(endDate)
                .storeId(storeId)
                .groupBy("category")
                .build();

        return ResponseEntity.ok(salesAnalysisService.analyzeSalesByCategory(request));
    }

    @GetMapping("/by-weather")
    public ResponseEntity<SalesAnalysisResponseDTO> getByWeather(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) Integer storeId) {

        if (startDate == null) {
            startDate = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS).minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }

        SalesAnalysisRequestDTO request = SalesAnalysisRequestDTO.builder()
                .startDate(startDate)
                .endDate(endDate)
                .storeId(storeId)
                .groupBy("weather")
                .build();

        return ResponseEntity.ok(salesAnalysisService.analyzeSalesByWeather(request));
    }
} 