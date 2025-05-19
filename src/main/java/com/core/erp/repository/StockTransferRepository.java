package com.core.erp.repository;

import com.core.erp.domain.StockTransferEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockTransferRepository extends JpaRepository<StockTransferEntity, Long> {
    // 특정 매장에서 이루어진 이동 조회 (출발 or 도착)
    List<StockTransferEntity> findByFromStore_StoreIdOrToStore_StoreId(int fromStoreId, int toStoreId);

    // 상품별 이동 이력 조회
    List<StockTransferEntity> findByProduct_ProductIdOrderByTransferredAtDesc(Long productId);

    // 담당자 기준 이동 이력 조회
    List<StockTransferEntity> findByTransferredBy_PartTimerId(int partTimerId);


}