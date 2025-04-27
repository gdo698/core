package com.core.erp.domain;

import com.core.erp.dto.SalesDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sales")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SalesEntity {

    @Id
    @Column(name = "sales_id")
    private int salesId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private StoreEntity store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emp_id", nullable = false)
    private EmployeeEntity employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @Column(name = "sales_total", nullable = false)
    private int salesTotal;

    @Column(name = "payment_method", nullable = false, length = 20)
    private String paymentMethod;

    @Column(name = "sales_time", nullable = false)
    private LocalDateTime salesTime;

    @Column(name = "sales_quantity", nullable = false)
    private int salesQuantity;

    @Column(name = "is_refunded", nullable = false)
    private int isRefunded;

    @Column(name = "discount_price", nullable = false)
    private int discountPrice;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "final_amount", nullable = false)
    private int finalAmount;

    @Column(name = "cost_price", nullable = false)
    private int costPrice;

    @Column(name = "real_income", nullable = false)
    private int realIncome;

    @Column(name = "is_settled", nullable = false)
    private int isSettled;

    @Column(name = "transaction_id")
    private Integer transactionId;

    // DTO → Entity 변환 생성자
    public SalesEntity(SalesDTO dto) {
        this.salesId = dto.getSalesId();
        // store, employee, product는 별도 매핑 필요
        this.salesTotal = dto.getSalesTotal();
        this.paymentMethod = dto.getPaymentMethod();
        this.salesTime = dto.getSalesTime();
        this.salesQuantity = dto.getSalesQuantity();
        this.isRefunded = dto.getIsRefunded();
        this.discountPrice = dto.getDiscountPrice();
        this.createdAt = dto.getCreatedAt();
        this.finalAmount = dto.getFinalAmount();
        this.costPrice = dto.getCostPrice();
        this.realIncome = dto.getRealIncome();
        this.isSettled = dto.getIsSettled();
        this.transactionId = dto.getTransactionId();
    }
}