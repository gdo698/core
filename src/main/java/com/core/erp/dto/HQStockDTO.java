package com.core.erp.dto;

import com.core.erp.domain.HQStockEntity;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class HQStockDTO {
    private int hqStockId;
    private int productId;
    private int quantity;
    private LocalDateTime lastUpdate;
    private LocalDateTime createdAt;
    private String updatedBy;
    
    // Entity → DTO 변환 생성자
    public HQStockDTO(HQStockEntity entity) {
        this.hqStockId = entity.getHqStockId();
        this.productId = entity.getProduct().getProductId();
        this.quantity = entity.getQuantity();
        this.lastUpdate = entity.getLastUpdate();
        this.createdAt = entity.getCreatedAt();
        this.updatedBy = entity.getUpdatedBy();
    }
}