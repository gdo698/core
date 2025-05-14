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
    private int totalQuantity;
    private Integer regularInDay;
    private Integer regularInQuantity;
    private Boolean regularInActive;
    private LocalDateTime lastUpdate;
    private LocalDateTime createdAt;
    private String updatedBy;
    private String productName;
    private Long barcode;
    private String categoryName;
    
    // Entity → DTO 변환 생성자
    public HQStockDTO(HQStockEntity entity) {
        this.hqStockId = entity.getHqStockId();
        this.productId = entity.getProduct().getProductId();
        this.quantity = entity.getQuantity();
        this.totalQuantity = entity.getTotalQuantity();
        this.regularInDay = entity.getRegularInDay();
        this.regularInQuantity = entity.getRegularInQuantity();
        this.regularInActive = entity.getRegularInActive();
        this.lastUpdate = entity.getLastUpdate();
        this.createdAt = entity.getCreatedAt();
        this.updatedBy = entity.getUpdatedBy();
        this.productName = entity.getProduct().getProName();
        this.barcode = entity.getProduct().getProBarcode();
        this.categoryName = entity.getProduct().getCategory() != null ? 
                          entity.getProduct().getCategory().getCategoryName() : "미분류";
    }
}