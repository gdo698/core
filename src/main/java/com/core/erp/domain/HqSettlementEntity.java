package com.core.erp.domain;

import com.core.pos.dto.SettlementDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "sales_settlement")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HqSettlementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "settlement_id")
    private Integer settlementId;

    @Column(name = "store_id", nullable = false)
    private Integer storeId;

    @Column(name = "emp_id")
    private Integer empId;

    @Column(name = "part_timer_id")
    private Integer partTimerId;

    @Column(name = "settlement_date", nullable = false)
    private LocalDate settlementDate;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "shift_start_time")
    private LocalDateTime shiftStartTime;

    @Column(name = "shift_end_time")
    private LocalDateTime shiftEndTime;

    @Column(name = "total_revenue", nullable = false)
    private Integer totalRevenue;

    @Column(name = "discount_total")
    private Integer discountTotal;

    @Column(name = "refund_total")
    private Integer refundTotal;

    @Column(name = "final_amount", nullable = false)
    private Integer finalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "settlement_type", nullable = false, length = 10)
    private SettlementType settlementType;

    @Column(name = "transaction_count")
    private Integer transactionCount;

    @Column(name = "refund_count")
    private Integer refundCount;

    @Column(name = "is_manual")
    private Integer isManual;

    @Column(name = "hq_sent_at")
    private LocalDateTime hqSentAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "hq_status", length = 10)
    private HqStatus hqStatus;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    // 정산 종류
    public enum SettlementType {
        DAILY, SHIFT, MONTHLY, YEARLY
    }

    // 본사 전송 상태
    public enum HqStatus {
        PENDING, SENT, FAILED
    }

    // DTO → Entity 변환 메서드
    public static HqSettlementEntity fromDTO(SettlementDTO dto) {
        return HqSettlementEntity.builder()
                .settlementId(dto.getSettlementId())
                .storeId(dto.getStoreId())
                .empId(dto.getEmpId())
                .partTimerId(dto.getPartTimerId())
                .settlementDate(dto.getSettlementDate())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .shiftStartTime(dto.getShiftStartTime())
                .shiftEndTime(dto.getShiftEndTime())
                .totalRevenue(dto.getTotalRevenue())
                .discountTotal(dto.getDiscountTotal())
                .refundTotal(dto.getRefundTotal())
                .finalAmount(dto.getFinalAmount())
                .settlementType(SettlementType.valueOf(dto.getSettlementType().name()))
                .transactionCount(dto.getTransactionCount())
                .refundCount(dto.getRefundCount())
                .isManual(dto.getIsManual())
                .hqSentAt(dto.getHqSentAt())
                .hqStatus(HqStatus.valueOf(dto.getHqStatus().name()))
                .build();
    }
}
