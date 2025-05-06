package com.core.erp.repository;

import com.core.erp.domain.StockAdjustLogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository

public interface StockAdjustLogRepository extends JpaRepository<StockAdjustLogEntity, Integer> {
    Page<StockAdjustLogEntity> findByStore_StoreId(Integer storeId, Pageable pageable);

    @Query("""
    SELECT l FROM StockAdjustLogEntity l
    WHERE (:storeId IS NULL OR l.store.storeId = :storeId)
      AND (:from IS NULL OR l.adjustDate >= :from)
      AND (:to IS NULL OR l.adjustDate <= :to)
      AND (:adjustedBy IS NULL OR l.adjustedBy LIKE %:adjustedBy%)
      AND (:productName IS NULL OR l.product.proName LIKE %:productName%)
""")
    Page<StockAdjustLogEntity> filterLogs(
            @Param("storeId") Integer storeId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("adjustedBy") String adjustedBy,
            @Param("productName") String productName,
            Pageable pageable
    );
}
