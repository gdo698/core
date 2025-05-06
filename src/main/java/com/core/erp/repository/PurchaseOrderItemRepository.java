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

}
