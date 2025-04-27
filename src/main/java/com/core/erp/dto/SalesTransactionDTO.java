package com.core.erp.dto;

import com.core.erp.domain.SalesTransactionEntity;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SalesTransactionDTO {

    private int transactionId;
    private Integer storeId; // FK (id만 관리)
    private Integer empId; // FK (id만 관리)
    private Integer  totalPrice;
    private Integer  discountTotal;
    private Integer  finalAmount;
    private String paymentMethod;
    private Integer  isRefunded;
    private String refundReason;
    private LocalDateTime paidAt;
    private LocalDateTime refundedAt;
    private LocalDateTime createdAt;

    // Entity → DTO 변환 생성자
    public SalesTransactionDTO(SalesTransactionEntity entity) {
        this.transactionId = entity.getTransactionId();
        this.storeId = entity.getStore() != null ? entity.getStore().getStoreId() : null;
        this.empId = entity.getEmployee() != null ? entity.getEmployee().getEmpId() : null;
        this.totalPrice = entity.getTotalPrice();
        this.discountTotal = entity.getDiscountTotal();
        this.finalAmount = entity.getFinalAmount();
        this.paymentMethod = entity.getPaymentMethod();
        this.isRefunded = entity.getIsRefunded();
        this.refundReason = entity.getRefundReason();
        this.paidAt = entity.getPaidAt();
        this.refundedAt = entity.getRefundedAt();
        this.createdAt = entity.getCreatedAt();

    }
}
