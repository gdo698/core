package com.core.erp.repository;

import com.core.erp.domain.StoreStockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StoreStockRepository extends JpaRepository<StoreStockEntity, Long>, StockRepositoryCustom {
    @Query("SELECT SUM(s.quantity) FROM StoreStockEntity s WHERE s.product.productId = :productId")
    Integer sumStockByProductId(Long productId);


    @Query("""
    SELECT s.quantity FROM StoreStockEntity s 
    WHERE s.product.productId = :productId AND s.store.storeId = :storeId
""")
    Optional<Integer> findQuantityByProductAndStore(@Param("productId") Long productId, @Param("storeId") Integer storeId);
    // ✅ 전체 재고 수량 합계 (productId 타입 통일)
    @Query("SELECT SUM(s.quantity) FROM StoreStockEntity s WHERE s.product.productId = :productId")
    Integer sumStockByProductId(@Param("productId") int productId);

    // ✅ 상품 ID로 매장 재고 목록 조회
    List<StoreStockEntity> findByProduct_ProductId(int productId);

    // ✅ 매장 ID + 상품 ID로 재고 엔티티 조회
    Optional<StoreStockEntity> findByStore_StoreIdAndProduct_ProductId(
            Integer storeId,
            int productId
    );

    // ✅ 매장 ID + 상품 ID로 수량만 조회
    @Query("""
        SELECT s.quantity 
        FROM StoreStockEntity s 
        WHERE s.product.productId = :productId 
          AND s.store.storeId = :storeId
    """)
    Optional<Integer> findQuantityByProductAndStore(
            @Param("productId") int productId,
            @Param("storeId") Integer storeId
    );
}