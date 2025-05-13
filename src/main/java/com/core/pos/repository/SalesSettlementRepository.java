package com.core.pos.repository;

import com.core.pos.domain.SalesSettlementEntity;
import com.core.pos.domain.SalesSettlementEntity.SettlementType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface SalesSettlementRepository extends JpaRepository<SalesSettlementEntity, Integer> {

    // 중복 정산 방지용 (store + date + type)
    Optional<SalesSettlementEntity> findByStoreIdAndSettlementDateAndSettlementType(Integer storeId, LocalDate date, SettlementType type);
}
