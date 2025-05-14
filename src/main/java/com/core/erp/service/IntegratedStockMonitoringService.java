package com.core.erp.service;

import com.core.erp.domain.*;
import com.core.erp.dto.*;
import com.core.erp.dto.category.CategoryDTO;
import com.core.erp.dto.stock.IntegratedStockDTO;
import com.core.erp.dto.stock.StockCategoryStatDTO;
import com.core.erp.dto.stock.StockStatusSummaryDTO;
import com.core.erp.dto.store.StoreDTO;
import com.core.erp.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 통합 재고 모니터링 서비스
 * 본사와 지점의 재고를 통합적으로 관리하기 위한 기능 제공
 */
@Service
@RequiredArgsConstructor
public class IntegratedStockMonitoringService {

    private final HQStockService hqStockService;
    private final BranchesStockMonitoringService branchStockService;
    private final StoreRepository storeRepository;
    private final CategoryRepository categoryRepository;
    private final HQStockRepository hqStockRepository;
    private final ProductRepository productRepository;
    private final BranchStockRepositoryImpl branchStockRepository;
    
    @PersistenceContext
    private final EntityManager entityManager;
    
    /**
     * 모든 지점 목록 조회
     * @return 지점 목록 (DTO 형태)
     */
    public List<StoreDTO> getAllBranches() {
        return branchStockService.getAllBranches();
    }

    /**
     * 모든 상품 카테고리 목록 조회
     * @return 카테고리 목록 (DTO 형태)
     */
    public List<CategoryDTO> getAllCategories() {
        return branchStockService.getAllCategories();
    }

    /**
     * 재고 현황 요약 정보 조회
     * @param viewMode 조회 모드 (integrated/headquarters/branches)
     * @param storeId 지점 ID (지점 모드 시)
     * @return 재고 상태별 개수
     */
    public StockStatusSummaryDTO getStockStatusSummary(String viewMode, Integer storeId) {
        switch (viewMode) {
            case "headquarters":
                return getHeadquartersStockSummary();
            case "branches":
                return branchStockService.getStockStatusSummary(storeId);
            case "integrated":
            default:
                return getIntegratedStockSummary(storeId);
        }
    }

    /**
     * 본사 재고 요약 생성
     * @return 본사 재고 상태별 개수
     */
    private StockStatusSummaryDTO getHeadquartersStockSummary() {
        List<HQStockDTO> hqStocks = hqStockService.getAllHQStocks();
        
        long dangerCount = 0;
        long warningCount = 0;
        long normalCount = 0;
        
        for (HQStockDTO stock : hqStocks) {
            int quantity = stock.getQuantity();
            if (quantity <= 100) { // 10% 미만
                dangerCount++;
            } else if (quantity <= 300) { // 30% 미만
                warningCount++;
            } else {
                normalCount++;
            }
        }
        
        return new StockStatusSummaryDTO(
                dangerCount,
                warningCount,
                normalCount,
                hqStocks.size()
        );
    }

    /**
     * 통합 재고 요약 생성 (본사 + 지점)
     * @param storeId 지점 ID (선택 시)
     * @return 통합 재고 상태별 개수
     */
    private StockStatusSummaryDTO getIntegratedStockSummary(Integer storeId) {
        // 본사 재고 요약 먼저 계산
        StockStatusSummaryDTO hqSummary = getHeadquartersStockSummary();
        
        // 지점 재고 요약 계산
        StockStatusSummaryDTO branchSummary = branchStockService.getStockStatusSummary(storeId);
        
        // 두 요약 정보 통합
        return new StockStatusSummaryDTO(
                hqSummary.getDangerCount() + branchSummary.getDangerCount(),
                hqSummary.getWarningCount() + branchSummary.getWarningCount(),
                hqSummary.getNormalCount() + branchSummary.getNormalCount(),
                hqSummary.getTotalCount() + branchSummary.getTotalCount()
        );
    }

    /**
     * 카테고리별 재고 통계 조회 (파이 차트용)
     * @param viewMode 조회 모드 (integrated/headquarters/branches)
     * @param storeId 지점 ID (지점 모드 시)
     * @return 카테고리별 재고 비율
     */
    public List<StockCategoryStatDTO> getCategoryStats(String viewMode, Integer storeId) {
        switch (viewMode) {
            case "headquarters":
                return getHeadquartersCategoryStats();
            case "branches":
                return branchStockService.getCategoryStats(storeId);
            case "integrated":
            default:
                return getIntegratedCategoryStats(storeId);
        }
    }

    /**
     * 본사 카테고리별 재고 통계 조회
     * @return 카테고리별 재고 비율
     */
    private List<StockCategoryStatDTO> getHeadquartersCategoryStats() {
        // HQ 재고와 상품, 카테고리 정보를 조인하여 통계 생성
        String jpql = "SELECT NEW map(" +
                "c.categoryId as categoryId, " +
                "c.categoryName as categoryName, " +
                "SUM(h.quantity) as quantity) " +
                "FROM HQStockEntity h " +
                "JOIN h.product p " +
                "JOIN p.category c " +
                "GROUP BY c.categoryId, c.categoryName";
        
        List<Map<String, Object>> results = entityManager.createQuery(jpql).getResultList();
        
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
     * 통합 카테고리별 재고 통계 조회 (본사 + 지점)
     * @param storeId 지점 ID (선택 시)
     * @return 카테고리별 재고 비율
     */
    private List<StockCategoryStatDTO> getIntegratedCategoryStats(Integer storeId) {
        // 본사 카테고리 통계 조회
        List<StockCategoryStatDTO> hqStats = getHeadquartersCategoryStats();
        
        // 지점 카테고리 통계 조회
        List<StockCategoryStatDTO> branchStats = branchStockService.getCategoryStats(storeId);
        
        // 카테고리별로 통합
        Map<Integer, StockCategoryStatDTO> mergedStats = new HashMap<>();
        
        // 본사 통계 먼저 병합
        hqStats.forEach(stat -> 
            mergedStats.put(stat.getCategoryId(), stat)
        );
        
        // 지점 통계 병합 (같은 카테고리면 수량 합산)
        branchStats.forEach(stat -> {
            if (mergedStats.containsKey(stat.getCategoryId())) {
                StockCategoryStatDTO existing = mergedStats.get(stat.getCategoryId());
                existing.setQuantity(existing.getQuantity() + stat.getQuantity());
            } else {
                mergedStats.put(stat.getCategoryId(), stat);
            }
        });
        
        // 전체 수량 계산하여 비율 다시 계산
        long totalQuantity = mergedStats.values().stream()
                .mapToLong(StockCategoryStatDTO::getQuantity)
                .sum();
        
        // 비율 업데이트
        mergedStats.values().forEach(stat -> {
            double percentage = totalQuantity > 0
                    ? (double) stat.getQuantity() / totalQuantity * 100
                    : 0;
            stat.setPercentage(Math.round(percentage * 100) / 100.0);
        });
        
        return new ArrayList<>(mergedStats.values());
    }

    /**
     * 지점별 재고 비교 조회 (바 차트용)
     * @return 지점별 재고 상태 비교 데이터
     */
    public List<Map<String, Object>> getBranchComparison() {
        return branchStockService.getBranchComparison();
    }

    /**
     * 본사 상품별 재고 조회
     * @param categoryId 카테고리 ID
     * @param productName 상품명 검색어
     * @param barcode 바코드
     * @return 본사 재고 목록
     */
    public List<HQStockDTO> getHeadquartersStock(Integer categoryId, String productName, Long barcode) {
        List<HQStockDTO> allStocks = hqStockService.getAllHQStocks();
        
        // 필터링
        return allStocks.stream()
                .filter(stock -> {
                    // 상품 정보 조회
                    ProductEntity product = productRepository.findById((long)stock.getProductId()).orElse(null);
                    if (product == null) return false;
                    
                    // 카테고리 필터
                    if (categoryId != null) {
                        CategoryEntity category = product.getCategory();
                        boolean matches = categoryId.equals(category.getCategoryId());
                        
                        // 상위 카테고리 확인 (중분류, 대분류)
                        if (!matches && category.getParentCategory() != null) {
                            matches = categoryId.equals(category.getParentCategory().getCategoryId());
                            
                            // 상위의 상위 카테고리 확인 (대분류)
                            if (!matches && category.getParentCategory().getParentCategory() != null) {
                                matches = categoryId.equals(category.getParentCategory().getParentCategory().getCategoryId());
                            }
                        }
                        if (!matches) return false;
                    }
                    
                    // 상품명 필터
                    if (productName != null && !productName.isEmpty()) {
                        if (!product.getProName().toLowerCase().contains(productName.toLowerCase())) {
                            return false;
                        }
                    }
                    
                    // 바코드 필터
                    if (barcode != null) {
                        if (!barcode.equals(product.getProBarcode())) {
                            return false;
                        }
                    }
                    
                    return true;
                })
                .collect(Collectors.toList());
    }

    /**
     * 통합 재고 상세 목록 조회 (필터링 및 페이지네이션)
     * @param viewMode 조회 모드 (integrated/headquarters/branches)
     * @param storeId 지점 ID
     * @param productName 상품명 검색어
     * @param barcode 바코드
     * @param categoryId 카테고리 ID
     * @param pageable 페이징 정보
     * @return 필터링된 재고 목록
     */
    public Page<IntegratedStockDTO> getFilteredStockList(
            String viewMode, Integer storeId, String productName, 
            Long barcode, Integer categoryId, Pageable pageable) {
        
        switch (viewMode) {
            case "headquarters":
                return getHeadquartersStockList(categoryId, productName, barcode, pageable);
            case "branches":
                return getBranchesStockList(storeId, productName, barcode, categoryId, pageable);
            case "integrated":
            default:
                return getIntegratedStockList(storeId, productName, barcode, categoryId, pageable);
        }
    }

    /**
     * 본사 재고 목록 조회
     */
    private Page<IntegratedStockDTO> getHeadquartersStockList(
            Integer categoryId, String productName, Long barcode, Pageable pageable) {
        
        // 본사 재고 조회
        List<HQStockDTO> hqStocks = getHeadquartersStock(categoryId, productName, barcode);
        
        // IntegratedStockDTO로 변환
        List<IntegratedStockDTO> result = new ArrayList<>();
        
        for (HQStockDTO hqStock : hqStocks) {
            result.add(IntegratedStockDTO.fromHQStock(
                    hqStock, 
                    hqStock.getProductName(), // DTO에서 직접 상품명 사용
                    hqStock.getCategoryName(), // DTO에서 직접 카테고리명 사용
                    hqStock.getBarcode() // DTO에서 직접 바코드 사용
            ));
        }
        
        // 페이징 처리
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), result.size());
        
        List<IntegratedStockDTO> pageContent = start < result.size() ? 
                result.subList(start, end) : new ArrayList<>();
                
        return new PageImpl<>(pageContent, pageable, result.size());
    }

    /**
     * 지점 재고 목록 조회
     */
    private Page<IntegratedStockDTO> getBranchesStockList(
            Integer storeId, String productName, Long barcode, 
            Integer categoryId, Pageable pageable) {
        
        // 지점 재고 조회
        Page<TotalStockDTO> branchStocks = branchStockService.getFilteredStockList(
                storeId, productName, barcode, categoryId, pageable);
        
        // IntegratedStockDTO로 변환
        List<IntegratedStockDTO> content = branchStocks.getContent().stream()
                .map(IntegratedStockDTO::new)
                .collect(Collectors.toList());
        
        return new PageImpl<>(content, pageable, branchStocks.getTotalElements());
    }

    /**
     * 통합 재고 목록 조회 (본사 + 지점)
     */
    private Page<IntegratedStockDTO> getIntegratedStockList(
            Integer storeId, String productName, Long barcode, 
            Integer categoryId, Pageable pageable) {
        
        // 페이징을 위해 먼저 전체 카운트를 얻어와야 함
        int totalSize = getTotalIntegratedStocksCount(storeId, productName, barcode, categoryId);
        
        // 페이지 정보에 따라 본사 또는 지점 재고를 가져옴
        int pageSize = pageable.getPageSize();
        int pageNumber = pageable.getPageNumber();
        int offset = pageNumber * pageSize;
        
        // 결과를 저장할 리스트
        List<IntegratedStockDTO> result = new ArrayList<>();
        
        // 1. 먼저 본사 재고 가져오기
        List<HQStockDTO> hqStocks = getHeadquartersStock(categoryId, productName, barcode);
        List<IntegratedStockDTO> hqStockDtos = new ArrayList<>();
        
        for (HQStockDTO hqStock : hqStocks) {
            hqStockDtos.add(IntegratedStockDTO.fromHQStock(
                    hqStock, 
                    hqStock.getProductName(), // DTO에서 직접 상품명 사용
                    hqStock.getCategoryName(), // DTO에서 직접 카테고리명 사용
                    hqStock.getBarcode() // DTO에서 직접 바코드 사용
            ));
        }
        
        // 2. 지점 재고 가져오기 - offset과 페이지 크기를 조정하여 가져옴
        Page<TotalStockDTO> branchStocks;
        
        // 본사 재고가 offset보다 많으면 본사 재고만 페이징해서 반환
        if (hqStockDtos.size() > offset) {
            int hqEndIndex = Math.min(offset + pageSize, hqStockDtos.size());
            result.addAll(hqStockDtos.subList(offset, hqEndIndex));
            
            // 나머지 페이지 공간에 지점 재고 추가
            if (result.size() < pageSize) {
                int remainingItems = pageSize - result.size();
                branchStocks = branchStockService.getFilteredStockList(
                        storeId, productName, barcode, categoryId, 
                        PageRequest.of(0, remainingItems, pageable.getSort()));
                
                result.addAll(branchStocks.getContent().stream()
                        .map(IntegratedStockDTO::new)
                        .collect(Collectors.toList()));
            }
        } 
        // 본사 재고보다 offset이 크면 지점 재고만 조회하고 페이징
        else {
            int branchOffset = offset - hqStockDtos.size();
            branchStocks = branchStockService.getFilteredStockList(
                    storeId, productName, barcode, categoryId,
                    PageRequest.of(branchOffset / pageSize, pageSize, pageable.getSort()));
            
            result.addAll(branchStocks.getContent().stream()
                    .map(IntegratedStockDTO::new)
                    .collect(Collectors.toList()));
        }
        
        return new PageImpl<>(result, pageable, totalSize);
    }
    
    /**
     * 통합 재고 전체 개수 조회
     */
    private int getTotalIntegratedStocksCount(
            Integer storeId, String productName, Long barcode, Integer categoryId) {
        
        // 본사 재고 카운트
        int hqCount = getHeadquartersStock(categoryId, productName, barcode).size();
        
        // 지점 재고 카운트 - 페이지 크기 1로 조회해서 전체 개수만 가져옴
        Page<TotalStockDTO> branchPage = branchStockService.getFilteredStockList(
                storeId, productName, barcode, categoryId, PageRequest.of(0, 1));
        
        return hqCount + (int) branchPage.getTotalElements();
    }
} 