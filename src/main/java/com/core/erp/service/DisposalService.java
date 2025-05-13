package com.core.erp.service;

import com.core.erp.domain.DisposalEntity;
import com.core.erp.dto.DisposalDTO;
import com.core.erp.dto.DisposalTargetDTO;
import com.core.erp.dto.DisposalTargetProjection;
import com.core.erp.repository.DisposalRepository;
import com.core.erp.repository.StoreStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DisposalService {

    private final StoreStockRepository storeStockRepository;
    private final DisposalRepository disposalRepository;

    // 폐기 대상 재고 조회 (유통기한 지난 것)
    public List<DisposalTargetDTO> getExpiredStocks() {
        List<DisposalTargetProjection> results = storeStockRepository.findExpiredDisposals();

        return results.stream()
                .map(p -> new DisposalTargetDTO(
                        p.getStockId(),
                        p.getProductId(),
                        p.getProName(),
                        p.getQuantity(),
                        p.getLastInDate(),
                        p.getExpiredDate()
                ))
                .collect(Collectors.toList());
    }

    // 폐기 내역 전체 조회 (정렬 포함)
    public List<DisposalDTO> getAllDisposals() {
        List<DisposalEntity> disposals = disposalRepository.findAllByOrderByDisposalDateDesc();
        return disposals.stream()
                .map(DisposalDTO::new)
                .collect(Collectors.toList());
    }

    // 특정 조건으로 폐기 내역 필터링
    public List<DisposalDTO> searchDisposals(String keyword, LocalDate start, LocalDate end) {
        LocalDateTime startDateTime = (start != null) ? start.atStartOfDay() : LocalDate.MIN.atStartOfDay();
        LocalDateTime endDateTime = (end != null) ? end.atTime(23, 59, 59) : LocalDate.MAX.atTime(23, 59, 59);

        List<DisposalEntity> disposals = disposalRepository
                .findByProduct_ProNameContainingAndDisposalDateBetweenOrderByDisposalDateDesc(
                        keyword, startDateTime, endDateTime
                );

        return disposals.stream()
                .map(DisposalDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void cancelDisposal(int disposalId) {
        DisposalEntity disposal = disposalRepository.findById(disposalId)
                .orElseThrow(() -> new IllegalArgumentException("폐기 내역을 찾을 수 없습니다."));

        disposalRepository.delete(disposal);
    }

}
