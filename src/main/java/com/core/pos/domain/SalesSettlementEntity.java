package com.core.pos.domain;

import com.core.pos.dto.SettlementRequestDTO;
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
public class SalesSettlementEntity {

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
    @Column(name = "settlement_type", nullable = false)
    private SettlementType settlementType;

    @Column(name = "transaction_count")
    private Integer transactionCount;

    @Column(name = "refund_count")
    private Integer refundCount;

    @Column(name = "is_manual")
    private Integer isManual; // 0: 자동, 1: 수동

    @Column(name = "hq_sent_at")
    private LocalDateTime hqSentAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "hq_status")
    private HqStatus hqStatus;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    public enum SettlementType {
        DAILY, MONTHLY, YEARLY, SHIFT
    }

    public enum HqStatus {
        PENDING, SENT, FAILED
    }

    public static SalesSettlementEntity fromRequestDTO(SettlementRequestDTO dto) {
        return SalesSettlementEntity.builder()
                .storeId(dto.getStoreId())
                .empId(dto.getEmpId())
                .partTimerId(dto.getPartTimerId())
                .settlementDate(dto.getTargetDate())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .shiftStartTime(dto.getShiftStartTime())
                .shiftEndTime(dto.getShiftEndTime())
                .isManual(dto.getIsManual())
                .build();
    }

}
