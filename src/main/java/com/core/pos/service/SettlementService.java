package com.core.pos.service;

import com.core.erp.repository.SalesTransactionRepository;
import com.core.pos.domain.SalesSettlementEntity;
import com.core.pos.domain.SalesSettlementEntity.HqStatus;
import com.core.pos.domain.SalesSettlementEntity.SettlementType;
import com.core.pos.dto.SettlementDTO;
import com.core.pos.dto.SettlementRequestDTO;
import com.core.pos.repository.SalesSettlementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SettlementService {

    private final SalesTransactionRepository transactionRepository;
    private final SalesSettlementRepository settlementRepository;
    private final SettlementSenderService senderService;

    // 일별 매출 정산 처리
    public void calculateDailySettlement(SettlementRequestDTO request) {
        Integer storeId = request.getStoreId();
        LocalDate date = request.getTargetDate();

        // 중복 방지
        if (settlementRepository.findByStoreIdAndSettlementDateAndSettlementType(storeId, date, SettlementType.daily).isPresent()) {
            throw new IllegalStateException("이미 정산이 완료된 날짜입니다.");
        }

        // 해당 날짜의 거래 데이터 필터링
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        // 해당 매장의 하루치 거래 내역 조회
        var transactions = transactionRepository.findByStoreStoreIdAndPaidAtBetween(storeId, start, end);

        // 정산 항목 계산
        int totalRevenue = 0;
        int discountTotal = 0;
        int refundTotal = 0;
        int finalAmount = 0;
        int transactionCount = 0;
        int refundCount = 0;

        for (var tx : transactions) {
            totalRevenue += tx.getTotalPrice();
            discountTotal += tx.getDiscountTotal();
            finalAmount += tx.getFinalAmount();
            if (tx.getTransactionStatus() == 1) { // 환불된 거래
                refundTotal += tx.getRefundAmount() != null ? tx.getRefundAmount() : 0;
                refundCount++;
            }
            transactionCount++;
        }

        // 저장
        SalesSettlementEntity entity = SalesSettlementEntity.builder()
                .storeId(storeId)
                .settlementDate(date)
                .startDate(null)
                .endDate(null)
                .totalRevenue(totalRevenue)
                .discountTotal(discountTotal)
                .refundTotal(refundTotal)
                .finalAmount(finalAmount)
                .settlementType(SettlementType.daily)
                .transactionCount(transactionCount)
                .refundCount(refundCount)
                .hqStatus(HqStatus.PENDING)
                .hqSentAt(null)
                .build();

        // 본사 전송 시도
        SettlementDTO dto = SettlementDTO.from(entity);
        boolean sent = senderService.sendToHeadOffice(dto);

        // 전송 결과에 따라 상태 업데이트
        entity.setHqStatus(sent ? HqStatus.SENT : HqStatus.FAILED);
        entity.setHqSentAt(LocalDateTime.now());

        // 정산 저장 (전송 상태 포함)
        settlementRepository.save(entity);
    }
}
