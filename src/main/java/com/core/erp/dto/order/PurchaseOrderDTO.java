package com.core.erp.dto.order;

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

    public PurchaseOrderDTO(Long orderId, int totalQuantity, int totalAmount, LocalDateTime orderDate, int orderStatus) {
        this.orderId = orderId;
        this.totalQuantity = totalQuantity;
        this.totalAmount = totalAmount;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
    }

}