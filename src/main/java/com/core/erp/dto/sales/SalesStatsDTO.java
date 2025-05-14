package com.core.erp.dto.sales;

import com.core.erp.domain.SalesStatsEntity;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SalesStatsDTO {

    private int salesStatsId;
    private Integer storeId; // FK
    private Integer productId; // FK
    private LocalDate sstDate;
    private int sstQuantity;
    private int sstTotal;
    private LocalDateTime createdAt;

    // Entity → DTO 변환 생성자
    public SalesStatsDTO(SalesStatsEntity entity) {
        this.salesStatsId = entity.getSalesStatsId();
        this.storeId = entity.getStore() != null ? entity.getStore().getStoreId() : null;
        this.productId = entity.getProduct() != null ? entity.getProduct().getProductId() : null;
        this.sstDate = entity.getSstDate();
        this.sstQuantity = entity.getSstQuantity();
        this.sstTotal = entity.getSstTotal();
        this.createdAt = entity.getCreatedAt();
    }
}