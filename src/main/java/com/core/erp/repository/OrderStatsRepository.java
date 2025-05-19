package com.core.erp.repository;

import com.core.erp.domain.OrderStatsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface OrderStatsRepository extends JpaRepository<OrderStatsEntity, Integer> {

    // 발주 통계 기준 상품별 순위 조회
    @Query("""
        SELECT o.product.proName,
               SUM(o.ostatsQuantity),
               SUM(o.ostatsTotal)
        FROM OrderStatsEntity o
        WHERE o.store.storeId = :storeId
          AND o.ostatsDate = :date
        GROUP BY o.product.productId
        ORDER BY SUM(o.ostatsQuantity) DESC
    """)
    List<Object[]> getTopOrderedProductsByStoreAndDate(
            @Param("storeId") Integer storeId,
            @Param("date") LocalDate date
    );
}
