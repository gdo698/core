package com.core.erp.domain;

import com.core.erp.dto.SalesStatsDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "sales_stats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SalesStatsEntity {

    @Id
    @Column(name = "sales_stats_id")
    private int salesStatsId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private StoreEntity store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @Column(name = "sst_date", nullable = false)
    private LocalDate sstDate;

    @Column(name = "sst_quantity", nullable = false)
    private int sstQuantity;

    @Column(name = "sst_total", nullable = false)
    private int sstTotal;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // DTO → Entity 변환 생성자
    public SalesStatsEntity(SalesStatsDTO dto) {
        this.salesStatsId = dto.getSalesStatsId();
        this.sstDate = dto.getSstDate();
        this.sstQuantity = dto.getSstQuantity();
        this.sstTotal = dto.getSstTotal();
        this.createdAt = dto.getCreatedAt();
    }
}