package com.core.erp.dto.order;

import com.core.erp.domain.OrderStatsEntity;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderStatsDTO {

    private int ostatsId;
    private Integer storeId; // FK (id만 관리)
    private Integer productId; // FK (id만 관리)
    private LocalDate ostatsDate;
    private int ostatsQuantity;
    private int ostatsTotal;
    private LocalDateTime createdAt;

    // Entity → DTO 변환 생성자
    public OrderStatsDTO(OrderStatsEntity entity) {
        this.ostatsId = entity.getOstatsId();
        this.storeId = entity.getStore() != null ? entity.getStore().getStoreId() : null;
        this.productId = entity.getProduct() != null ? entity.getProduct().getProductId() : null;
        this.ostatsDate = entity.getOstatsDate();
        this.ostatsQuantity = entity.getOstatsQuantity();
        this.ostatsTotal = entity.getOstatsTotal();
        this.createdAt = entity.getCreatedAt();
    }
}