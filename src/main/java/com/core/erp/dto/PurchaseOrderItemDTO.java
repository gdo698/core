package com.core.erp.dto;

import com.core.erp.domain.PurchaseOrderItemEntity;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PurchaseOrderItemDTO {

    private int itemId;
    private Integer orderId; // FK (id만 관리)
    private Integer productId; // FK (id만 관리)
    private int orderQuantity;
    private int unitPrice;
    private int totalPrice;
    private int orderState;
    private int isAbnormal;
    private int isFullyReceived;
    private Integer receivedQuantity;

    private MultipartFile file;

    // Entity → DTO 변환 생성자
    public PurchaseOrderItemDTO(PurchaseOrderItemEntity entity) {
        this.itemId = entity.getItemId();
        this.orderId = entity.getPurchaseOrder() != null ? entity.getPurchaseOrder().getOrderId() : null;
        this.productId = entity.getProduct() != null ? entity.getProduct().getProductId() : null;
        this.orderQuantity = entity.getOrderQuantity();
        this.unitPrice = entity.getUnitPrice();
        this.totalPrice = entity.getTotalPrice();
        this.orderState = entity.getOrderState();
        this.isAbnormal = entity.getIsAbnormal();
        this.isFullyReceived = entity.getIsFullyReceived();
        this.receivedQuantity = entity.getReceivedQuantity();
    }
}