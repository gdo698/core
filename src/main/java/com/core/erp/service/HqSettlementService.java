package com.core.erp.service;

import com.core.erp.domain.HqSettlementEntity;
import com.core.erp.repository.HqSettlementRepository;
import com.core.pos.dto.SettlementDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class HqSettlementService {

    private final HqSettlementRepository repository;

    public void saveSettlement(SettlementDTO dto) {
        // 변환
        HqSettlementEntity entity = HqSettlementEntity.fromDTO(dto);

        // 중복 여부 확인 (store + date + type 조합)
        boolean exists = repository.existsByStoreIdAndSettlementDateAndSettlementType(
                entity.getStoreId(),
                entity.getSettlementDate(),
                entity.getSettlementType()
        );

        if (exists) {
            log.warn("⚠️ 중복된 정산이므로 저장 생략 - storeId: {}, date: {}, type: {}",
                    entity.getStoreId(), entity.getSettlementDate(), entity.getSettlementType());
            return;
        }

        // 저장
        repository.save(entity);
        log.info("✅ 정산 저장 완료 - ID: {}, 매장: {}, 금액: {}원",
                entity.getSettlementId(), entity.getStoreId(), entity.getFinalAmount());
    }
}
