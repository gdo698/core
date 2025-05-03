package com.core.erp.domain;

import com.core.erp.dto.PurchaseOrderItemDTO;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "purchase_order_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PurchaseOrderItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private int itemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private PurchaseOrderEntity purchaseOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @Column(name = "order_quantity", nullable = false)
    private int orderQuantity;

    @Column(name = "unit_price", nullable = false)
    private int unitPrice;

    @Column(name = "total_price", nullable = false)
    private int totalPrice;

    @Column(name = "order_state", nullable = false)
    private int orderState;

    @Column(name = "is_abnormal", nullable = false)
    private int isAbnormal;

    @Column(name = "is_fully_received", nullable = false)
    private int isFullyReceived;

    @Column(name = "received_quantity")
    private Integer receivedQuantity;

    // DTO → Entity 변환 생성자
    public PurchaseOrderItemEntity(PurchaseOrderItemDTO dto) {
        this.itemId = dto.getItemId();
        // purchaseOrder, product는 별도 매핑 필요
        this.orderQuantity = dto.getOrderQuantity();
        this.unitPrice = dto.getUnitPrice();
        this.totalPrice = dto.getTotalPrice();
        this.orderState = dto.getOrderState();
        this.isAbnormal = dto.getIsAbnormal();
        this.isFullyReceived = dto.getIsFullyReceived();
        this.receivedQuantity = dto.getReceivedQuantity();
    }
}