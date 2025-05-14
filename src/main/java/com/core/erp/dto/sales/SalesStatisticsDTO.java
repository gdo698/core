package com.core.erp.dto.sales;

import com.core.erp.domain.SalesStatisticsEntity;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SalesStatisticsDTO {

    private int statsId;
    private Integer storeId; // FK (id만 관리)
    private Integer categoryId; // FK (id만 관리)
    private LocalDate date;
    private LocalDateTime hour;
    private Double totalSales;
    private int transactionCount;
    private Double avgTransaction;
    private LocalDateTime createdAt;

    // Entity → DTO 변환 생성자
    public SalesStatisticsDTO(SalesStatisticsEntity entity) {
        this.statsId = entity.getStatsId();
        this.storeId = entity.getStore() != null ? entity.getStore().getStoreId() : null;
        this.categoryId = entity.getCategory() != null ? entity.getCategory().getCategoryId() : null;
        this.date = entity.getDate();
        this.hour = entity.getHour();
        this.totalSales = entity.getTotalSales();
        this.transactionCount = entity.getTransactionCount();
        this.avgTransaction = entity.getAvgTransaction();
        this.createdAt = entity.getCreatedAt();
    }
}