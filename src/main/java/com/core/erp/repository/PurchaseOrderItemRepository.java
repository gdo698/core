package com.core.erp.repository;

import com.core.erp.domain.PurchaseOrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseOrderItemRepository extends JpaRepository<PurchaseOrderItemEntity, Long> {
    List<PurchaseOrderItemEntity> findByPurchaseOrder_OrderId(Long orderId);

    @Query("""
SELECT i FROM PurchaseOrderItemEntity i
WHERE i.purchaseOrder.store.storeId = :storeId
  AND i.orderState = 0
  AND i.isFullyReceived = 0
""")
    List<PurchaseOrderItemEntity> findPendingItemsByStore(@Param("storeId") Integer storeId);

    void deleteByPurchaseOrder_OrderId(Long orderId);

    // 발주 상품 순위 (실시간): 매장 + 날짜 기준 상품별 발주 수량/금액 집계
    @Query("""
    SELECT i.product.proName,
           SUM(i.orderQuantity),
           SUM(i.totalPrice)
    FROM PurchaseOrderItemEntity i
    WHERE i.purchaseOrder.store.storeId = :storeId
      AND FUNCTION('DATE', i.purchaseOrder.orderDate) = :date
      AND i.orderState = 1
    GROUP BY i.product.productId
    ORDER BY SUM(i.orderQuantity) DESC
""")
    List<Object[]> getTopOrderedProductsByStoreAndDate(
            @Param("storeId") Integer storeId,
            @Param("date") java.time.LocalDate date
    );


}
