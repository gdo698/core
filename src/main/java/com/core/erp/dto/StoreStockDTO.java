package com.core.erp.dto;

import com.core.erp.domain.StoreStockEntity;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StoreStockDTO {

    private int stockId;
    private Integer storeId; // FK (id만 관리)
    private Integer productId; // FK (id만 관리)
    private int quantity;
    private LocalDateTime lastInDate;
    private int stockStatus;

    private MultipartFile file;

    // Entity → DTO 변환 생성자
    public StoreStockDTO(StoreStockEntity entity) {
        this.stockId = entity.getStockId();
        this.storeId = entity.getStore() != null ? entity.getStore().getStoreId() : null;
        this.productId = entity.getProduct() != null ? entity.getProduct().getProductId() : null;
        this.quantity = entity.getQuantity();
        this.lastInDate = entity.getLastInDate();
        this.stockStatus = entity.getStockStatus();
    }
}