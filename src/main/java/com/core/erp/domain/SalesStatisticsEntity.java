package com.core.erp.domain;

import com.core.erp.dto.SalesStatisticsDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "sales_statistics")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SalesStatisticsEntity {

    @Id
    @Column(name = "stats_id")
    private int statsId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private StoreEntity store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryEntity category;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "hour")
    private LocalDateTime hour;

    @Column(name = "total_sales", nullable = false)
    private Double totalSales;

    @Column(name = "transaction_count", nullable = false)
    private int transactionCount;

    @Column(name = "avg_transaction", nullable = false)
    private Double avgTransaction;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // DTO → Entity 변환 생성자
    public SalesStatisticsEntity(SalesStatisticsDTO dto) {
        this.statsId = dto.getStatsId();
        // store, category는 별도 매핑 필요
        this.date = dto.getDate();
        this.hour = dto.getHour();
        this.totalSales = dto.getTotalSales();
        this.transactionCount = dto.getTransactionCount();
        this.avgTransaction = dto.getAvgTransaction();
        this.createdAt = dto.getCreatedAt();
    }
}