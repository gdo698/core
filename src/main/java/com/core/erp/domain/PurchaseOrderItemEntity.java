package com.core.erp.domain;

import com.core.erp.dto.order.PurchaseOrderItemDTO;
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

    @Column(name = "received_quantity", nullable = false)
    private Integer receivedQuantity = 0;

    @Column(name = "product_name", nullable = false)
    private String productName;


    // DTO → Entity 변환 생성자
    public PurchaseOrderItemEntity(PurchaseOrderItemDTO dto) {
        this.itemId = dto.getItemId();
        this.orderQuantity = dto.getOrderQuantity();
        this.unitPrice = dto.getUnitPrice();
        this.totalPrice = dto.getTotalPrice();
        this.orderState = dto.getOrderState();
        this.isAbnormal = dto.getIsAbnormal();
        this.isFullyReceived = dto.getIsFullyReceived();
        this.receivedQuantity = dto.getReceivedQuantity();
        this.productName = dto.getProductName();
    }
}