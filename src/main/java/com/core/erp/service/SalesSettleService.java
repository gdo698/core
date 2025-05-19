package com.core.erp.service;

import com.core.erp.domain.SalesSettleEntity;
import com.core.erp.domain.SalesSettleEntity.SettlementType;

import com.core.erp.dto.sales.SalesSettleDTO;
import com.core.erp.repository.SalesSettleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalesSettleService {

    private final SalesSettleRepository salesSettleRepository;

    public List<SalesSettleDTO> getSettlements(Integer storeId, LocalDate startDate, LocalDate endDate, SettlementType type) {
        List<SalesSettleEntity> settlements;

        if (startDate == null || endDate == null) {
            // 날짜가 없으면 전체 조회
            if (type == null) {
                settlements = salesSettleRepository.findByStore_StoreIdAndSettlementTypeIn(
                        storeId, List.of(SettlementType.DAILY, SettlementType.SHIFT)
                );
            } else {
                settlements = salesSettleRepository.findByStore_StoreIdAndSettlementType(
                        storeId, type
                );
            }
        } else {
            // 날짜가 있는 경우
            if (type == null) {
                settlements = salesSettleRepository.findByStore_StoreIdAndSettlementDateBetweenAndSettlementTypeIn(
                        storeId, startDate, endDate, List.of(SettlementType.DAILY, SettlementType.SHIFT)
                );
            } else {
                settlements = salesSettleRepository.findByStore_StoreIdAndSettlementDateBetweenAndSettlementType(
                        storeId, startDate, endDate, type
                );
            }
        }

        return settlements.stream()
                .map(SalesSettleDTO::new)
                .collect(Collectors.toList());
    }

}
