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

    List<StoreStockEntity> findByProduct_ProductId(int productId);

    Optional<StoreStockEntity> findByStore_StoreIdAndProduct_ProductId(Integer storeId, Integer productId);

    @Query("""
    SELECT s.quantity FROM StoreStockEntity s 
    WHERE s.product.productId = :productId AND s.store.storeId = :storeId
""")
    Optional<Integer> findQuantityByProductAndStore(@Param("productId") Long productId, @Param("storeId") Integer storeId);}