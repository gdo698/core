package com.core.pos.domain;

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

    @Column(name = "settlement_date", nullable = false)
    private LocalDate settlementDate;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

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
        daily, monthly, yearly
    }

    public enum HqStatus {
        PENDING, SENT, FAILED
    }
}
