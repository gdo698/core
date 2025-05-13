package com.core.erp.service;

import com.core.erp.domain.CategoryEntity;
import com.core.erp.domain.StoreEntity;
import com.core.erp.domain.StoreStockEntity;
import com.core.erp.dto.*;
import com.core.erp.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 지점별 재고 모니터링 서비스
 * 본사 지점관리팀을 위한 기능 제공
 */
@Service
@RequiredArgsConstructor
public class BranchesStockMonitoringService {

    private final StoreRepository storeRepository;
    private final CategoryRepository categoryRepository;
    private final BranchStockRepositoryImpl branchStockRepository;
    
    @PersistenceContext
    private final EntityManager entityManager;

    /**
     * 모든 지점 목록 조회
     * @return 지점 목록 (DTO 형태)
     */
    public List<StoreDTO> getAllBranches() {
        List<StoreEntity> stores = storeRepository.findAll();
        return stores.stream()
                .map(StoreDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * 모든 상품 카테고리 목록 조회
     * @return 카테고리 목록 (DTO 형태)
     */
    public List<CategoryDTO> getAllCategories() {
        // 대분류 카테고리만 조회 (categoryFilter = 1)
        List<CategoryEntity> categories = categoryRepository.findAll();
        return categories.stream()
                .filter(c -> c.getCategoryFilter() != null && c.getCategoryFilter() == 1)
                .map(CategoryDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * 재고 현황 요약 정보 조회
     * @param storeId 지점 ID (전체 조회 시 null)
     * @return 재고 상태별 개수
     */
    public StockStatusSummaryDTO getStockStatusSummary(Integer storeId) {
        StringBuilder jpql = new StringBuilder();
        jpql.append("SELECT NEW map(")
            .append("SUM(CASE WHEN ss.stockStatus = 4 THEN 1 ELSE 0 END) as dangerCount, ")
            .append("SUM(CASE WHEN ss.stockStatus = 2 THEN 1 ELSE 0 END) as warningCount, ")
            .append("SUM(CASE WHEN ss.stockStatus = 1 THEN 1 ELSE 0 END) as normalCount, ")
            .append("COUNT(ss) as totalCount) ")
            .append("FROM StoreStockEntity ss ");
        
        if (storeId != null) {
            jpql.append("WHERE ss.store.storeId = :storeId");
        }
        
        Query query = entityManager.createQuery(jpql.toString());
        
        if (storeId != null) {
            query.setParameter("storeId", storeId);
        }
        
        Map<String, Object> result = (Map<String, Object>) query.getSingleResult();
        
        return new StockStatusSummaryDTO(
                ((Number) result.get("dangerCount")).longValue(),
                ((Number) result.get("warningCount")).longValue(),
                ((Number) result.get("normalCount")).longValue(),
                ((Number) result.get("totalCount")).longValue()
        );
    }

    /**
     * 카테고리별 재고 통계 조회 (파이 차트용)
     * @param storeId 지점 ID (전체 조회 시 null)
     * @return 카테고리별 재고 비율
     */
    public List<StockCategoryStatDTO> getCategoryStats(Integer storeId) {
        StringBuilder jpql = new StringBuilder();
        jpql.append("SELECT NEW map(")
            .append("c.categoryId as categoryId, ")
            .append("c.categoryName as categoryName, ")
            .append("SUM(ss.quantity) as quantity) ")
            .append("FROM StoreStockEntity ss ")
            .append("JOIN ss.product p ")
            .append("JOIN p.category c ");
        
        if (storeId != null) {
            jpql.append("WHERE ss.store.storeId = :storeId ");
        }
        
        jpql.append("GROUP BY c.categoryId, c.categoryName ");
        
        Query query = entityManager.createQuery(jpql.toString());
        
        if (storeId != null) {
            query.setParameter("storeId", storeId);
        }
        
        List<Map<String, Object>> results = query.getResultList();
        
        // 전체 재고 수량 계산
        long totalQuantity = results.stream()
                .mapToLong(map -> ((Number) map.get("quantity")).longValue())
                .sum();
        
        // 결과를 DTO로 변환하면서 비율 계산
        return results.stream()
                .map(map -> {
                    long quantity = ((Number) map.get("quantity")).longValue();
                    double percentage = totalQuantity > 0 
                            ? (double) quantity / totalQuantity * 100 
                            : 0;
                    
                    return new StockCategoryStatDTO(
                            ((Number) map.get("categoryId")).intValue(),
                            (String) map.get("categoryName"),
                            quantity,
                            Math.round(percentage * 100) / 100.0 // 소수점 둘째자리까지 반올림
                    );
                })
                .collect(Collectors.toList());
    }

    /**
     * 지점별 재고 비교 조회 (바 차트용)
     * @return 지점별 재고 상태 비교 데이터
     */
    public List<Map<String, Object>> getBranchComparison() {
        StringBuilder jpql = new StringBuilder();
        jpql.append("SELECT NEW map(")
            .append("s.storeId as storeId, ")
            .append("s.storeName as storeName, ")
            .append("SUM(CASE WHEN ss.stockStatus = 1 THEN 1 ELSE 0 END) as normalCount, ")
            .append("SUM(CASE WHEN ss.stockStatus = 2 THEN 1 ELSE 0 END) as warningCount, ")
            .append("SUM(CASE WHEN ss.stockStatus = 4 THEN 1 ELSE 0 END) as dangerCount) ")
            .append("FROM StoreStockEntity ss ")
            .append("JOIN ss.store s ")
            .append("GROUP BY s.storeId, s.storeName ")
            .append("ORDER BY s.storeId");
        
        Query query = entityManager.createQuery(jpql.toString());
        
        List<Map<String, Object>> results = query.getResultList();
        
        // 결과를 차트에 맞게 변환
        return results.stream()
                .map(map -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("name", map.get("storeName"));
                    item.put("정상재고", map.get("normalCount"));
                    item.put("경고재고", map.get("warningCount"));
                    item.put("긴급재고", map.get("dangerCount"));
                    return item;
                })
                .collect(Collectors.toList());
    }

    /**
     * 재고 상세 목록 조회 (필터링 및 페이지네이션)
     * @param storeId 지점 ID
     * @param productName 상품명 검색어
     * @param categoryId 카테고리 ID
     * @param pageable 페이징 정보
     * @return 필터링된 재고 목록
     */
    public Page<TotalStockDTO> getFilteredStockList(
            Integer storeId,
            String productName,
            Long barcode,
            Integer categoryId,
            Pageable pageable) {
        
        return branchStockRepository.findBranchStockSummary(
                storeId, 
                productName, 
                barcode, 
                categoryId, 
                pageable);
    }
} 