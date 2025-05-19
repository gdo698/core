package com.core.pos.repository;

import com.core.pos.domain.SalesSettlementEntity;
import com.core.pos.domain.SalesSettlementEntity.SettlementType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface SalesSettlementRepository extends JpaRepository<SalesSettlementEntity, Integer> {

    // 중복 정산 방지용 (store + date + type 조합 존재 여부 확인)
    boolean existsByStoreIdAndSettlementDateAndSettlementType(Integer storeId, LocalDate settlementDate, SettlementType settlementType);

    // 최근 정산 2건 조회 (내림차순 정렬)
    List<SalesSettlementEntity> findTop2ByStoreIdOrderBySettlementDateDesc(Integer storeId);
}
