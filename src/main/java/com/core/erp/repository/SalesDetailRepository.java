package com.core.erp.repository;

import com.core.erp.domain.SalesDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface SalesDetailRepository extends JpaRepository<SalesDetailEntity, Integer>, SalesDetailRepositoryCustom {

    @Query("SELECT d FROM SalesDetailEntity d " + "JOIN FETCH d.product p " +
            "LEFT JOIN FETCH p.category " + "WHERE d.transaction.transactionId = :transactionId")
    List<SalesDetailEntity> findWithProductByTransactionId(@Param("transactionId") Integer transactionId);

    List<SalesDetailEntity> findByTransaction_TransactionId(Integer transactionId);

    // KPI: 오늘 날짜 기준, 특정 매장의 총 판매 수량
    @Query("""
SELECT COALESCE(SUM(d.salesQuantity), 0)
FROM SalesDetailEntity d
WHERE d.transaction.store.storeId = :storeId
  AND d.transaction.paidAt BETWEEN :start AND :end
  AND d.transaction.transactionStatus = 0
""")
    int sumSalesQuantityByStoreAndPeriod(
            @Param("storeId") Integer storeId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    // 상품별 매출 순위: 매장과 날짜 기준으로 product별 판매 수량 집계 (TOP 10)
    @Query("""
    SELECT d.product.proName,
           SUM(d.salesQuantity),
           d.product.category.categoryName
    FROM SalesDetailEntity d
    WHERE d.transaction.store.storeId = :storeId
      AND FUNCTION('DATE', d.transaction.paidAt) = :date
      AND d.transaction.transactionStatus = 0
    GROUP BY d.product.productId
    ORDER BY SUM(d.salesQuantity) DESC
""")
    List<Object[]> getTopProductSalesByStoreAndDate(
            @Param("storeId") Integer storeId,
            @Param("date") LocalDate date
    );

    // 카테고리별 매출 비율: 매장과 날짜 기준, category별 총 매출 집계
    @Query("""
        SELECT d.product.category.categoryName,
               SUM(d.unitPrice * d.salesQuantity - d.discountPrice)
        FROM SalesDetailEntity d
        WHERE d.transaction.store.storeId = :storeId
          AND FUNCTION('DATE', d.transaction.paidAt) = :date
          AND d.transaction.transactionStatus = 0
        GROUP BY d.product.category.categoryId
    """)
    List<Object[]> getCategorySalesByStoreAndDate(
            @Param("storeId") Integer storeId,
            @Param("date") LocalDate date
    );

    // 시간대별 매출 통계: 특정 매장의 특정 날짜에 시간별 매출 합계 조회
    @Query("""
SELECT FUNCTION('HOUR', t.paidAt),
       SUM(d.salesQuantity),
       SUM(d.finalAmount)
FROM SalesDetailEntity d
JOIN d.transaction t
WHERE t.store.storeId = :storeId
  AND t.paidAt BETWEEN :start AND :end
  AND t.transactionStatus = 0
GROUP BY FUNCTION('HOUR', t.paidAt)
ORDER BY FUNCTION('HOUR', t.paidAt)
""")
    List<Object[]> getHourlySalesByStoreAndPeriod(
            @Param("storeId") Integer storeId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

}

