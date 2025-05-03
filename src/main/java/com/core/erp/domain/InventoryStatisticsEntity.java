package com.core.erp.domain;

import com.core.erp.dto.InventoryStatisticsDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_statistics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class InventoryStatisticsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stats_id")
    private int statsId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private StoreEntity store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryEntity category;

    @Column(name = "inven_date", nullable = false)
    private LocalDate invenDate;

    @Column(name = "inven_turnover_rate", nullable = false)
    private Double invenTurnoverRate;

    @Column(name = "inven_stock_value", nullable = false)
    private Double invenStockValue;

    @Column(name = "inven_low_stock_count", nullable = false)
    private int invenLowStockCount;

    @Column(name = "inven_excess_stock_count", nullable = false)
    private int invenExcessStockCount;

    @Column(name = "inven_expired_soon_count", nullable = false)
    private int invenExpiredSoonCount;

    @Column(name = "inven_created_at")
    private LocalDateTime invenCreatedAt;

    // DTO → Entity 변환 생성자
    public InventoryStatisticsEntity(InventoryStatisticsDTO dto) {
        this.statsId = dto.getStatsId();
        // store, category는 별도 매핑 필요
        this.invenDate = dto.getInvenDate();
        this.invenTurnoverRate = dto.getInvenTurnoverRate();
        this.invenStockValue = dto.getInvenStockValue();
        this.invenLowStockCount = dto.getInvenLowStockCount();
        this.invenExcessStockCount = dto.getInvenExcessStockCount();
        this.invenExpiredSoonCount = dto.getInvenExpiredSoonCount();
        this.invenCreatedAt = dto.getInvenCreatedAt();
    }
}