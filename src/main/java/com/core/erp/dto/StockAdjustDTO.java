package com.core.erp.dto;

import com.core.erp.domain.ProductEntity;
import com.core.erp.domain.StockAdjustLogEntity;
import com.core.erp.domain.StoreEntity;
import lombok.*;

import java.time.LocalDateTime;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString

    public class StockAdjustDTO {
    private Integer storeId;
    private Integer productId;
    private Integer newQuantity;
    private String reason;
    private Integer partTimerId;

    public StockAdjustLogEntity toEntity(StoreEntity store, ProductEntity product, String adjustedBy, int prevQuantity) {
        StockAdjustLogEntity entity = new StockAdjustLogEntity();
        entity.setStore(store);
        entity.setProduct(product);
        entity.setPrevQuantity(prevQuantity);
        entity.setNewQuantity(this.newQuantity);
        entity.setAdjustReason(this.reason);
        entity.setAdjustedBy(adjustedBy); // partTimer 이름 등
        entity.setAdjustDate(LocalDateTime.now());
        return entity;
    }


}