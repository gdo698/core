package com.core.erp.dto.stock;

import com.core.erp.domain.InventoryStatisticsEntity;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class InventoryStatisticsDTO {

    private int statsId;
    private Integer storeId;
    private Integer categoryId;
    private LocalDate invenDate;
    private Double invenTurnoverRate;
    private Double invenStockValue;
    private int invenLowStockCount;
    private int invenExcessStockCount;
    private int invenExpiredSoonCount;
    private LocalDateTime invenCreatedAt;


    // Entity → DTO 변환 생성자
    public InventoryStatisticsDTO(InventoryStatisticsEntity entity) {
        this.statsId = entity.getStatsId();
        this.storeId = entity.getStore() != null ? entity.getStore().getStoreId() : null;
        this.categoryId = entity.getCategory() != null ? entity.getCategory().getCategoryId() : null;
        this.invenDate = entity.getInvenDate();
        this.invenTurnoverRate = entity.getInvenTurnoverRate();
        this.invenStockValue = entity.getInvenStockValue();
        this.invenLowStockCount = entity.getInvenLowStockCount();
        this.invenExcessStockCount = entity.getInvenExcessStockCount();
        this.invenExpiredSoonCount = entity.getInvenExpiredSoonCount();
        this.invenCreatedAt = entity.getInvenCreatedAt();
    }
}