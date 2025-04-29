package com.core.erp.repository;

import com.core.erp.domain.StoreStockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface StoreStockRepository extends JpaRepository<StoreStockEntity, Long> {
    @Query("SELECT SUM(s.quantity) FROM StoreStockEntity s WHERE s.product.productId = :productId")
    Integer sumStockByProductId(Long productId);

    List<StoreStockEntity> findByProduct_ProductId(int productId);
}