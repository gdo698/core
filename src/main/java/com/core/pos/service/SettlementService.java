package com.core.pos.service;

import com.core.erp.domain.SalesTransactionEntity;
import com.core.erp.dto.sales.SalesSettleDTO;
import com.core.erp.repository.SalesTransactionRepository;
import com.core.pos.domain.SalesSettlementEntity;
import com.core.pos.domain.SalesSettlementEntity.HqStatus;
import com.core.pos.domain.SalesSettlementEntity.SettlementType;
import com.core.pos.dto.*;
import com.core.pos.repository.SalesSettlementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SettlementService {

    private final SalesTransactionRepository transactionRepository;
    private final SalesSettlementRepository settlementRepository;
    private final SettlementSenderService senderService;

    // 일별 매출 정산 처리
    public SettlementDTO calculateDailySettlement(SettlementRequestDTO request) {
        Integer storeId = request.getStoreId();
        Integer empId = request.getEmpId(); // 수동 정산일 경우 점주 ID
        LocalDate date = request.getTargetDate();

        // 중복 정산 방지
        if (settlementRepository.existsByStoreIdAndSettlementDateAndSettlementType(storeId, date, SettlementType.DAILY)) {
            throw new IllegalStateException("이미 정산이 완료된 날짜입니다.");
        }

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        var transactions = transactionRepository.findByStoreStoreIdAndPaidAtBetween(storeId, start, end);

        int totalRevenue = 0;
        int discountTotal = 0;
        int refundTotal = 0;
        int transactionCount = 0;
        int refundCount = 0;

        for (var tx : transactions) {
            transactionCount++;

            totalRevenue += tx.getTotalPrice();
            discountTotal += tx.getDiscountTotal();

            if (tx.getTransactionStatus() == 1) {
                refundCount++;
                refundTotal += tx.getRefundAmount() != null ? tx.getRefundAmount() : 0;
            }
        }

        int finalAmount = totalRevenue - discountTotal - refundTotal;

        SalesSettlementEntity entity = SalesSettlementEntity.builder()
                .storeId(storeId)
                .empId(empId)
                .partTimerId(null)
                .settlementDate(date)
                .startDate(date)
                .endDate(date)
                .shiftStartTime(null)
                .shiftEndTime(null)
                .totalRevenue(totalRevenue)
                .discountTotal(discountTotal)
                .refundTotal(refundTotal)
                .finalAmount(finalAmount)
                .settlementType(SettlementType.valueOf(request.getType().toUpperCase()))
                .transactionCount(transactionCount)
                .refundCount(refundCount)
                .isManual(request.getIsManual()) // DTO 기반으로 정확히 입력
                .hqStatus(HqStatus.PENDING)
                .hqSentAt(null)
                .build();

        // 본사 전송
        SettlementDTO dto = SettlementDTO.from(entity);
        boolean sent = senderService.sendToHeadOffice(dto);

        entity.setHqStatus(sent ? HqStatus.SENT : HqStatus.FAILED);
        entity.setHqSentAt(LocalDateTime.now());

        settlementRepository.save(entity);

        return SettlementDTO.from(entity);

    }


    // 교대 매출 정산 처리
    public SettlementDTO calculateShiftSettlement(ShiftSettlementRequestDTO request) {
        Integer storeId = request.getStoreId();
        Integer empId = request.getEmpId();
        Integer partTimerId = request.getPartTimerId();
        LocalDateTime start = request.getShiftStartTime();
        LocalDateTime end = request.getShiftEndTime();
        LocalDate date = request.getSettlementDate();

        int totalRevenue = 0;
        int discountTotal = 0;
        int refundTotal = 0;
        int transactionCount = 0;
        int refundCount = 0;

        var transactions = transactionRepository.findByStoreStoreIdAndPaidAtBetween(storeId, start, end);

        for (var tx : transactions) {
            transactionCount++;
            if (tx.getTransactionStatus() == 1) {
                refundCount++;
                refundTotal += tx.getRefundAmount() != null ? tx.getRefundAmount() : 0;
            } else {
                totalRevenue += tx.getTotalPrice();
                discountTotal += tx.getDiscountTotal();
            }
        }

        int finalAmount = totalRevenue - discountTotal - refundTotal;

        SalesSettlementEntity entity = SalesSettlementEntity.builder()
                .storeId(storeId)
                .empId(empId)
                .partTimerId(partTimerId)
                .settlementDate(date)
                .startDate(null)
                .endDate(null)
                .shiftStartTime(start)
                .shiftEndTime(end)
                .totalRevenue(totalRevenue)
                .discountTotal(discountTotal)
                .refundTotal(refundTotal)
                .finalAmount(finalAmount)
                .settlementType(SettlementType.valueOf(request.getType().toUpperCase()))
                .transactionCount(transactionCount)
                .refundCount(refundCount)
                .isManual(request.getIsManual())
                .hqStatus(HqStatus.PENDING)
                .hqSentAt(null)
                .build();

        SettlementDTO dto = SettlementDTO.from(entity);
        boolean sent = senderService.sendToHeadOffice(dto);

        entity.setHqStatus(sent ? HqStatus.SENT : HqStatus.FAILED);
        entity.setHqSentAt(LocalDateTime.now());

        settlementRepository.save(entity);

        return SettlementDTO.from(entity);
    }

    // 월별 매출 정산 처리
    public SettlementDTO  calculateMonthlySettlement(MonthlySettlementRequestDTO request) {
        Integer storeId = request.getStoreId();
        int year = request.getYear();
        int month = request.getMonth();

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        // settlement_date는 해당 월의 마지막 날로 사용
        LocalDate settlementDate = endDate;

        // 중복 정산 방지
        boolean exists = settlementRepository.existsByStoreIdAndSettlementDateAndSettlementType(
                storeId, settlementDate, SettlementType.MONTHLY);

        if (exists) {
            throw new IllegalStateException("이미 해당 월의 월별 정산이 존재합니다.");
        }

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);

        var transactions = transactionRepository.findByStoreStoreIdAndPaidAtBetween(storeId, start, end);

        int totalRevenue = 0;
        int discountTotal = 0;
        int refundTotal = 0;
        int transactionCount = 0;
        int refundCount = 0;

        for (var tx : transactions) {
            transactionCount++;
            totalRevenue += tx.getTotalPrice();
            discountTotal += tx.getDiscountTotal();

            if (tx.getTransactionStatus() == 1) {
                refundCount++;
                refundTotal += tx.getRefundAmount() != null ? tx.getRefundAmount() : 0;
            }
        }

        int finalAmount = totalRevenue - discountTotal - refundTotal;

        SalesSettlementEntity entity = SalesSettlementEntity.builder()
                .storeId(storeId)
                .empId(null) // 시스템 자동정산이므로 null 처리 가능
                .partTimerId(null)
                .settlementDate(settlementDate)
                .startDate(startDate)
                .endDate(endDate)
                .shiftStartTime(null)
                .shiftEndTime(null)
                .totalRevenue(totalRevenue)
                .discountTotal(discountTotal)
                .refundTotal(refundTotal)
                .finalAmount(finalAmount)
                .settlementType(SettlementType.MONTHLY)
                .transactionCount(transactionCount)
                .refundCount(refundCount)
                .isManual(0) // 자동 정산
                .hqStatus(HqStatus.PENDING)
                .hqSentAt(null)
                .build();

        // 본사 전송
        SettlementDTO dto = SettlementDTO.from(entity);
        boolean sent = senderService.sendToHeadOffice(dto);

        entity.setHqStatus(sent ? HqStatus.SENT : HqStatus.FAILED);
        entity.setHqSentAt(LocalDateTime.now());

        return SettlementDTO.from(entity);
    }

    // 연별 매출 정산 처리
    public SettlementDTO calculateYearlySettlement(YearlySettlementRequestDTO request) {
        Integer storeId = request.getStoreId();
        int year = request.getYear();

        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);
        LocalDate settlementDate = endDate;

        // 중복 정산 방지
        boolean exists = settlementRepository.existsByStoreIdAndSettlementDateAndSettlementType(
                storeId, settlementDate, SettlementType.YEARLY);
        if (exists) {
            throw new IllegalStateException("이미 해당 연도의 연별 정산이 존재합니다.");
        }

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);

        var transactions = transactionRepository.findByStoreStoreIdAndPaidAtBetween(storeId, start, end);

        int totalRevenue = 0;
        int discountTotal = 0;
        int refundTotal = 0;
        int transactionCount = 0;
        int refundCount = 0;

        for (var tx : transactions) {
            transactionCount++;
            totalRevenue += tx.getTotalPrice();
            discountTotal += tx.getDiscountTotal();

            if (tx.getTransactionStatus() == 1) {
                refundCount++;
                refundTotal += tx.getRefundAmount() != null ? tx.getRefundAmount() : 0;
            }
        }

        int finalAmount = totalRevenue - discountTotal - refundTotal;

        SalesSettlementEntity entity = SalesSettlementEntity.builder()
                .storeId(storeId)
                .empId(null)
                .partTimerId(null)
                .settlementDate(settlementDate)
                .startDate(startDate)
                .endDate(endDate)
                .shiftStartTime(null)
                .shiftEndTime(null)
                .totalRevenue(totalRevenue)
                .discountTotal(discountTotal)
                .refundTotal(refundTotal)
                .finalAmount(finalAmount)
                .settlementType(SettlementType.YEARLY)
                .transactionCount(transactionCount)
                .refundCount(refundCount)
                .isManual(0)
                .hqStatus(HqStatus.PENDING)
                .hqSentAt(null)
                .build();

        SettlementDTO dto = SettlementDTO.from(entity);
        boolean sent = senderService.sendToHeadOffice(dto);

        entity.setHqStatus(sent ? HqStatus.SENT : HqStatus.FAILED);
        entity.setHqSentAt(LocalDateTime.now());

        settlementRepository.save(entity);
        return SettlementDTO.from(entity);
    }



    // 정산 미리보기 (저장 없이 계산만)
    public SettlementPreviewDTO getPreviewSummary(Integer storeId, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        List<SalesTransactionEntity> transactions = transactionRepository.findByStoreStoreIdAndPaidAtBetween(storeId, start, end);

        int totalRevenue = 0;
        int discountTotal = 0;
        int refundTotal = 0;
        int transactionCount = 0;
        int refundCount = 0;

        for (var tx : transactions) {
            transactionCount++;

            if (tx.getTransactionStatus() == 1) {
                refundCount++;
                refundTotal += tx.getRefundAmount() != null ? tx.getRefundAmount() : 0;
            } else {
                totalRevenue += tx.getTotalPrice();
                discountTotal += tx.getDiscountTotal();
            }
        }

        int finalAmount = totalRevenue - discountTotal - refundTotal;

        return SettlementPreviewDTO.builder()
                .totalRevenue(totalRevenue)
                .discountTotal(discountTotal)
                .refundTotal(refundTotal)
                .finalAmount(finalAmount)
                .transactionCount(transactionCount)
                .refundCount(refundCount)
                .build();

    }

    // 최근 정산 이력 조회 (2건)
    public List<SettlementPreviewDTO> getRecentSettlements(Integer storeId) {
        return settlementRepository.findTop2ByStoreIdOrderBySettlementDateDesc(storeId).stream()
                .map(entity -> SettlementPreviewDTO.builder()
                        .settlementDate(entity.getSettlementDate())
                        .settlementType(entity.getSettlementType() != null ? entity.getSettlementType().name() : null)
                        .isManual(entity.getIsManual())
                        .hqStatus(entity.getHqStatus() != null ? entity.getHqStatus().name() : null)
                        .totalRevenue(entity.getTotalRevenue())
                        .discountTotal(entity.getDiscountTotal())
                        .refundTotal(entity.getRefundTotal())
                        .finalAmount(entity.getFinalAmount())
                        .transactionCount(entity.getTransactionCount())
                        .refundCount(entity.getRefundCount())
                        .build())
                .collect(Collectors.toList());
    }


    }



