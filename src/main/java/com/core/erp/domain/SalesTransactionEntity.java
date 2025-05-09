package com.core.erp.domain;

import com.core.erp.dto.SalesTransactionDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sales_transaction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SalesTransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Integer transactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private StoreEntity store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emp_id")
    private EmployeeEntity employee;

    @Column(name = "total_price", nullable = false)
    private Integer totalPrice;

    @Column(name = "discount_total")
    private Integer discountTotal;

    @Column(name = "final_amount", nullable = false)
    private Integer finalAmount;

    @Column(name = "payment_method", nullable = false, length = 20)
    private String paymentMethod;

    @Column(name = "is_refunded")
    private Integer isRefunded;

    @Column(name = "refund_reason")
    private String refundReason;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "refunded_at")
    private LocalDateTime refundedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "age_group")
    private Integer ageGroup;

    @Column(name = "gender", length = 10)
    private String gender;

    // DTO → Entity 변환 생성자
    public SalesTransactionEntity(SalesTransactionDTO dto) {
        this.transactionId = dto.getTransactionId();
        // store, employee는 별도 매핑 필요
        this.totalPrice = dto.getTotalPrice();
        this.discountTotal = dto.getDiscountTotal();
        this.finalAmount = dto.getFinalAmount();
        this.paymentMethod = dto.getPaymentMethod();
        this.isRefunded = dto.getIsRefunded();
        this.refundReason = dto.getRefundReason();
        this.paidAt = dto.getPaidAt();
        this.refundedAt = dto.getRefundedAt();
        this.createdAt = dto.getCreatedAt();
        this.ageGroup = dto.getAgeGroup();
        this.gender = dto.getGender();
    }
}
