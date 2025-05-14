package com.core.erp.controller;

import com.core.erp.dto.*;
import com.core.erp.dto.category.CategoryDTO;
import com.core.erp.dto.stock.IntegratedStockDTO;
import com.core.erp.dto.stock.StockCategoryStatDTO;
import com.core.erp.dto.stock.StockStatusSummaryDTO;
import com.core.erp.dto.store.StoreDTO;
import com.core.erp.service.IntegratedStockMonitoringService;
import com.core.erp.service.HQStockService;
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
 * 통합 재고 모니터링 API 컨트롤러
 * 본사 및 지점 재고를 통합 관리하기 위한 API 제공
 */
@RestController
@RequestMapping("/api/integrated-stock")
@RequiredArgsConstructor
public class IntegratedStockMonitoringController {

    private final IntegratedStockMonitoringService integratedStockService;
    private final HQStockService hqStockService;

    /**
     * 통합 재고 현황 요약 정보 조회 API
     * @param viewMode 조회 모드 (integrated/headquarters/branches)
     * @param storeId 지점 ID (지점 모드 시)
     * @return 재고 상태별(정상, 경고, 긴급) 개수
     */
    @GetMapping("/summary")
    public ResponseEntity<StockStatusSummaryDTO> getStockStatusSummary(
            @RequestParam(defaultValue = "integrated") String viewMode,
            @RequestParam(required = false) Integer storeId) {
        
        // 페이지 로드시 본사 재고 전체 재계산
        try {
            hqStockService.recalculateAllHQStocks();
        } catch (Exception e) {
            // 재계산 실패해도 요약 정보는 보여주기 위해 로그만 남김
            System.err.println("통합 재고 모니터링 페이지 접속 시 본사 재고 재계산 실패: " + e.getMessage());
        }
        
        return ResponseEntity.ok(integratedStockService.getStockStatusSummary(viewMode, storeId));
    }

    /**
     * 카테고리별 재고 통계 조회 API (파이 차트용)
     * @param viewMode 조회 모드 (integrated/headquarters/branches)
     * @param storeId 지점 ID (지점 모드 시)
     * @return 카테고리별 재고 비율
     */
    @GetMapping("/category-stats")
    public ResponseEntity<List<StockCategoryStatDTO>> getCategoryStats(
            @RequestParam(defaultValue = "integrated") String viewMode,
            @RequestParam(required = false) Integer storeId) {
        return ResponseEntity.ok(integratedStockService.getCategoryStats(viewMode, storeId));
    }

    /**
     * 지점별 재고 비교 API (바 차트용)
     * @return 지점별 재고 상태 비교 데이터
     */
    @GetMapping("/branch-comparison")
    public ResponseEntity<List<Map<String, Object>>> getBranchComparison() {
        return ResponseEntity.ok(integratedStockService.getBranchComparison());
    }
    
    /**
     * 본사 상품별 재고 조회 API
     * @return 본사 재고 목록
     */
    @GetMapping("/headquarters")
    public ResponseEntity<List<HQStockDTO>> getHeadquartersStock(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) Long barcode) {
        return ResponseEntity.ok(integratedStockService.getHeadquartersStock(categoryId, productName, barcode));
    }
    
    /**
     * 통합 재고 상세 목록 조회 API (필터링 및 페이지네이션 지원)
     * @param viewMode 조회 모드 (integrated/headquarters/branches)
     * @param storeId 지점 ID
     * @param productName 상품명 검색어
     * @param barcode 바코드
     * @param categoryId 카테고리 ID
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return 필터링된 재고 목록
     */
    @GetMapping("/list")
    public ResponseEntity<Page<IntegratedStockDTO>> getFilteredStockList(
            @RequestParam(defaultValue = "integrated") String viewMode,
            @RequestParam(required = false) Integer storeId,
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) Long barcode,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("productName").ascending());
        
        Page<IntegratedStockDTO> result = integratedStockService.getFilteredStockList(
                viewMode, storeId, productName, barcode, categoryId, pageable);
                
        return ResponseEntity.ok(result);
    }

    /**
     * 모든 지점 목록 조회 API
     * @return 지점 목록
     */
    @GetMapping("/branches")
    public ResponseEntity<List<StoreDTO>> getAllBranches() {
        return ResponseEntity.ok(integratedStockService.getAllBranches());
    }

    /**
     * 상품 카테고리 목록 조회 API
     * @return 카테고리 목록
     */
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        return ResponseEntity.ok(integratedStockService.getAllCategories());
    }
} 