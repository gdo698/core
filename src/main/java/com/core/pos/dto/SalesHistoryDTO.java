package com.core.pos.dto;

import com.core.erp.domain.SalesTransactionEntity;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SalesHistoryDTO {

    private Integer transactionId;
    private String paymentMethod;
    private Integer finalAmount;
    private LocalDateTime paidAt;

    // 환불 관련 필드
    private Integer isRefunded;
    private String refundReason;
    private LocalDateTime refundedAt;

    // 상품 요약 정보
    private Integer totalPrice;
    private Integer discountTotal;
    private List<SaleItemSummaryDTO> items;

    // 변환 생성자
    public SalesHistoryDTO(SalesTransactionEntity entity, List<SaleItemSummaryDTO> items) {
        this.transactionId = entity.getTransactionId();
        this.paymentMethod = entity.getPaymentMethod();
        this.totalPrice = entity.getTotalPrice();
        this.discountTotal = entity.getDiscountTotal();
        this.finalAmount = entity.getFinalAmount();
        this.paidAt = entity.getPaidAt();
        this.isRefunded = entity.getIsRefunded();
        this.refundReason = entity.getRefundReason();
        this.refundedAt = entity.getRefundedAt();
        this.items = items;

    }
}