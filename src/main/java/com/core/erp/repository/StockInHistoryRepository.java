package com.core.erp.repository;

import com.core.erp.domain.StockInHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StockInHistoryRepository extends JpaRepository<StockInHistoryEntity, Long> {
    List<StockInHistoryEntity> findTop3ByProduct_ProductIdOrderByInDateDesc(int productId);
}