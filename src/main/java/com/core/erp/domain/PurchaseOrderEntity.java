package com.core.erp.domain;

import com.core.erp.dto.PurchaseOrderDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "purchase_order")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PurchaseOrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private int orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private StoreEntity store;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    @Column(name = "order_status", nullable = false)
    private int orderStatus;

    @Column(name = "total_amount", nullable = false)
    private int totalAmount;

    @Column(name = "total_quantity", nullable = false)
    private int totalQuantity;

    // DTO → Entity 변환 생성자
    public PurchaseOrderEntity(PurchaseOrderDTO dto) {
        this.orderId = dto.getOrderId();
        // store는 별도 매핑 필요
        this.orderDate = dto.getOrderDate();
        this.orderStatus = dto.getOrderStatus();
        this.totalAmount = dto.getTotalAmount();
        this.totalQuantity = dto.getTotalQuantity();
    }
}