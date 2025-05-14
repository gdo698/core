package com.core.erp.controller;

import com.core.erp.dto.stock.StockCategoryStatDTO;
import com.core.erp.dto.stock.StockStatusSummaryDTO;
import com.core.erp.dto.TotalStockDTO;
import com.core.erp.service.BranchesStockMonitoringService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 지점별 재고 모니터링 API 컨트롤러
 * 본사 지점관리팀 전용 API
 */
@RestController
@RequestMapping("/api/headquarters/branches/stock")
@RequiredArgsConstructor
public class BranchesStockMonitoringController {

    private final BranchesStockMonitoringService stockMonitoringService;

    /**
     * 모든 지점 목록 조회 API
     * @return 모든 지점 목록
     */
    @GetMapping("/branches")
    public ResponseEntity<?> getAllBranches() {
        return ResponseEntity.ok(stockMonitoringService.getAllBranches());
    }

    /**
     * 상품 카테고리 목록 조회 API
     * @return 카테고리 목록
     */
    @GetMapping("/categories")
    public ResponseEntity<?> getAllCategories() {
        return ResponseEntity.ok(stockMonitoringService.getAllCategories());
    }

    /**
     * 재고 현황 요약 정보 조회 API
     * @param storeId 지점 ID (전체 조회 시 null)
     * @return 재고 상태별(정상, 경고, 긴급) 개수
     */
    @GetMapping("/summary")
    public ResponseEntity<StockStatusSummaryDTO> getStockStatusSummary(
            @RequestParam(required = false) Integer storeId) {
        return ResponseEntity.ok(stockMonitoringService.getStockStatusSummary(storeId));
    }

    /**
     * 카테고리별 재고 통계 조회 API (파이 차트용)
     * @param storeId 지점 ID (전체 조회 시 null)
     * @return 카테고리별 재고 비율
     */
    @GetMapping("/category-stats")
    public ResponseEntity<List<StockCategoryStatDTO>> getCategoryStats(
            @RequestParam(required = false) Integer storeId) {
        return ResponseEntity.ok(stockMonitoringService.getCategoryStats(storeId));
    }

    /**
     * 지점별 재고 비교 API (바 차트용)
     * @return 지점별 재고 상태 비교 데이터
     */
    @GetMapping("/branch-comparison")
    public ResponseEntity<List<Map<String, Object>>> getBranchComparison() {
        return ResponseEntity.ok(stockMonitoringService.getBranchComparison());
    }

    /**
     * 재고 상세 목록 조회 API (필터링 및 페이지네이션 지원)
     * @param storeId 지점 ID
     * @param productName 상품명 검색어
     * @param categoryId 카테고리 ID
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return 필터링된 재고 목록
     */
    @GetMapping("/list")
    public ResponseEntity<?> getFilteredStockList(
            @RequestParam(required = false) Integer storeId,
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) Long barcode,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("productName").ascending());
            
            Page<TotalStockDTO> result = stockMonitoringService.getFilteredStockList(
                    storeId, productName, barcode, categoryId, pageable);
                    
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            // 오류 로그 기록
            e.printStackTrace();
            
            // 사용자 친화적인 오류 메시지 반환
            String errorMessage = "재고 목록을 불러오는 중 오류가 발생했습니다.";
            
            // 더 구체적인 오류 정보 제공 (개발 환경에서만)
            if (e.getMessage() != null && !e.getMessage().isBlank()) {
                errorMessage = e.getMessage();
            }
            
            return ResponseEntity.status(500).body(errorMessage);
        }
    }
} 