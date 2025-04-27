package com.core.erp.dto;

import com.core.erp.domain.PurchaseOrderEntity;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PurchaseOrderDTO {

    private int orderId;
    private Integer storeId; // FK (id만 관리)
    private LocalDateTime orderDate;
    private int orderStatus;
    private int totalAmount;
    private int totalQuantity;

    private MultipartFile file;

    // Entity → DTO 변환 생성자
    public PurchaseOrderDTO(PurchaseOrderEntity entity) {
        this.orderId = entity.getOrderId();
        this.storeId = entity.getStore() != null ? entity.getStore().getStoreId() : null;
        this.orderDate = entity.getOrderDate();
        this.orderStatus = entity.getOrderStatus();
        this.totalAmount = entity.getTotalAmount();
        this.totalQuantity = entity.getTotalQuantity();
    }
}