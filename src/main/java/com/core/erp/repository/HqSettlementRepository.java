package com.core.erp.repository;

import com.core.erp.domain.HqSettlementEntity;
import com.core.erp.domain.HqSettlementEntity.SettlementType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface HqSettlementRepository extends JpaRepository<HqSettlementEntity, Integer> {

    // 중복 저장 방지를 위한 체크용 (선택사항)
    boolean existsByStoreIdAndSettlementDateAndSettlementType(
            Integer storeId, LocalDate settlementDate, SettlementType type
    );

}
