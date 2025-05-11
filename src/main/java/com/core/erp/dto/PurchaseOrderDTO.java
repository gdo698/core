package com.core.erp.dto;

import com.core.erp.domain.PurchaseOrderEntity;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class PurchaseOrderDTO {

    private Long orderId;;
    private Integer storeId;
    private LocalDateTime orderDate;
    private int orderStatus;
    private int totalAmount;
    private int totalQuantity;

    //  Native Query 결과 매핑용 생성자 (파라미터 순서 주의!)
    public PurchaseOrderDTO(Long orderId, int totalQuantity, int totalAmount, LocalDateTime orderDate, int orderStatus) {
        this.orderId = orderId;
        this.totalQuantity = totalQuantity;
        this.totalAmount = totalAmount;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
    }

    //  Entity → DTO 변환 생성자
    public PurchaseOrderDTO(PurchaseOrderEntity entity) {
        this.orderId = entity.getOrderId();
        this.storeId = entity.getStore() != null ? entity.getStore().getStoreId() : null;
        this.orderDate = entity.getOrderDate();
        this.orderStatus = entity.getOrderStatus();
        this.totalAmount = entity.getTotalAmount();
        this.totalQuantity = entity.getTotalQuantity();
    }
}