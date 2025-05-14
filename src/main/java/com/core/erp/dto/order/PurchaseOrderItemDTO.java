package com.core.erp.dto.order;

import com.core.erp.domain.PurchaseOrderItemEntity;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PurchaseOrderItemDTO {

    private int itemId;
    private Long orderId; // FK (id만 관리)
    private Integer productId; // FK (id만 관리)
    private String productName;
    private int orderQuantity;
    private int unitPrice;
    private int totalPrice;
    private int orderState;
    private int isAbnormal;
    private int isFullyReceived;
    private Integer receivedQuantity;

    // Entity → DTO 변환 생성자
    public PurchaseOrderItemDTO(PurchaseOrderItemEntity entity) {
        this.itemId = entity.getItemId();
        this.orderId = entity.getPurchaseOrder() != null ? entity.getPurchaseOrder().getOrderId() : null;
        this.productId = entity.getProduct() != null ? entity.getProduct().getProductId() : null;
        this.productName = entity.getProduct() != null ? entity.getProduct().getProName() : null;
        this.orderQuantity = entity.getOrderQuantity();
        this.unitPrice = entity.getUnitPrice();
        this.totalPrice = entity.getTotalPrice();
        this.orderState = entity.getOrderState();
        this.isAbnormal = entity.getIsAbnormal();
        this.isFullyReceived = entity.getIsFullyReceived();
        this.receivedQuantity = entity.getReceivedQuantity();
    }
}