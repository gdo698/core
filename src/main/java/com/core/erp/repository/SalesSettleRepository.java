package com.core.erp.repository;

import com.core.erp.domain.SalesSettleEntity;
import com.core.erp.domain.SalesSettleEntity.SettlementType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SalesSettleRepository extends JpaRepository<SalesSettleEntity, Integer> {

    // 날짜 + type 여러 개 (ALL에 날짜 있을 때 사용)
    List<SalesSettleEntity> findByStore_StoreIdAndSettlementDateBetweenAndSettlementTypeIn(
            Integer storeId, LocalDate startDate, LocalDate endDate, List<SettlementType> types
    );

    // 날짜 + type 하나
    List<SalesSettleEntity> findByStore_StoreIdAndSettlementDateBetweenAndSettlementType(
            Integer storeId, LocalDate startDate, LocalDate endDate, SettlementType type
    );

    // 날짜 없이 type 여러 개 (ALL + 전체 날짜 조회)
    List<SalesSettleEntity> findByStore_StoreIdAndSettlementTypeIn(
            Integer storeId, List<SettlementType> types
    );

    // 날짜 없이 type 하나 (DAILY, SHIFT 중 하나, 전체 날짜 조회)
    List<SalesSettleEntity> findByStore_StoreIdAndSettlementType(
            Integer storeId, SettlementType type
    );
}

