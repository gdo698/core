package com.core.erp.repository;

import com.core.erp.domain.WarehouseStockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface WarehouseStockRepository extends JpaRepository<WarehouseStockEntity, Long> {

    // 창고 수량 조회 (productId: int, storeId: int로 통일)
    @Query("""
        SELECT w.quantity 
        FROM WarehouseStockEntity w 
        WHERE w.product.productId = :productId 
          AND w.store.storeId = :storeId
    """)
    Optional<Integer> findQuantityByProductAndStore(
            @Param("productId") int productId,
            @Param("storeId") int storeId
    );

    // 창고 재고 엔티티 조회 (타입 통일)
    Optional<WarehouseStockEntity> findByStore_StoreIdAndProduct_ProductId(
            Integer storeId,
            int productId
    );
}
