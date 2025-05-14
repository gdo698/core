package com.core.erp.repository;

import com.core.erp.domain.StockFlowEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockFlowRepository extends JpaRepository<StockFlowEntity, Long> {

    //  상품별 로그 조회 (최근순)
//    List<StockFlowEntity> findByProduct_ProductIdOrderByFlowDateDesc(Long productId);

    //  지점 + 상품으로 필터링
    Page<StockFlowEntity> findByStore_StoreIdAndProduct_ProductIdOrderByFlowDateDesc(
            Integer storeId, Long productId, Pageable pageable
    );

//    //  기간 + 상품 필터링 (재고 흐름 분석용)
//    @Query("""
//        SELECT f FROM StockFlowEntity f
//        WHERE f.product.productId = :productId
//        AND f.flowDate BETWEEN :startDate AND :endDate
//        ORDER BY f.flowDate DESC
//    """)
//    List<StockFlowEntity> findFlowByProductAndDateRange(
//            @Param("productId") Long productId,
//            @Param("startDate") LocalDateTime startDate,
//            @Param("endDate") LocalDateTime endDate
//    );
//
//    //  특정 흐름 유형만 조회 (ex. 입고만 보기)
//    List<StockFlowEntity> findByStore_StoreIdAndProduct_ProductIdAndFlowTypeOrderByFlowDateDesc(
//            Integer storeId, Long productId, Integer flowType
//    );

    @Query("""

            SELECT f FROM StockFlowEntity f
WHERE (:storeId IS NULL OR f.store.storeId = :storeId)
  AND (:productId IS NULL OR f.product.productId = :productId)
  AND (:productName IS NULL OR f.product.proName LIKE %:productName%)
  AND (:flowType IS NULL OR f.flowType = :flowType)
  AND (:startDate IS NULL OR f.flowDate >= :startDate)
  AND (:endDate IS NULL OR f.flowDate <= :endDate)
ORDER BY f.flowDate DESC
""")
    Page<StockFlowEntity> searchStockFlows(
            @Param("storeId") Integer storeId,
            @Param("productId") Long productId,
            @Param("productName") String productName,
            @Param("flowType") Integer flowType,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );
}
