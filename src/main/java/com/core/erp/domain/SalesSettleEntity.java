package com.core.erp.domain;

import com.core.erp.dto.SalesSettleDTO;
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
public class SalesSettleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "settlement_id")
    private Integer settlementId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private StoreEntity store;

    @Column(name = "settlement_date", nullable = false)
    private LocalDate settlementDate;

    @Column(name = "total_revenue", nullable = false)
    private Integer totalRevenue;

    @Column(name = "discount_total")
    private Integer discountTotal;

    @Column(name = "final_amount", nullable = false)
    private Integer finalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "settlement_type", nullable = false, length = 10)
    private SettlementType settlementType;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    public enum SettlementType {
        daily, monthly, yearly
    }

    // DTO → Entity 변환 생성자
    public SalesSettleEntity(SalesSettleDTO dto, StoreEntity storeEntity) {
        this.settlementId = dto.getSettlementId();
        this.store = storeEntity;
        this.settlementDate = dto.getSettlementDate();
        this.totalRevenue = dto.getTotalRevenue();
        this.discountTotal = dto.getDiscountTotal();
        this.finalAmount = dto.getFinalAmount();
        this.settlementType = dto.getSettlementType();
        this.createdAt = dto.getCreatedAt();
        this.updatedAt = dto.getUpdatedAt();
    }
}
