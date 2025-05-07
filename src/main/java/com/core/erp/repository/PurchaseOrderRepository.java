package com.core.erp.repository;

import com.core.erp.domain.PurchaseOrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrderEntity, Long> {
    Page<PurchaseOrderEntity> findByStore_StoreIdOrderByOrderIdDesc(Integer storeId, Pageable pageable);

    // 발주 제한 repo
    boolean existsByStore_StoreIdAndOrderDateBetween(Integer storeId, LocalDateTime start, LocalDateTime end);
}