package com.core.erp.repository;

import com.core.erp.domain.PurchaseOrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrderEntity, Long> {
    Page<PurchaseOrderEntity> findByStore_StoreIdOrderByOrderIdDesc(Integer storeId, Pageable pageable);
}