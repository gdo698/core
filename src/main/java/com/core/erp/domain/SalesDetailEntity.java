package com.core.erp.domain;

import com.core.erp.dto.SalesDetailDTO;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sales_detail")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SalesDetailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sales_detail_id")
    private Integer salesDetailId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    private SalesTransactionEntity transaction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @Column(name = "sales_quantity", nullable = false)
    private Integer salesQuantity;

    @Column(name = "unit_price", nullable = false)
    private Integer unitPrice;

    @Column(name = "discount_price")
    private Integer discountPrice;

    @Column(name = "final_amount", nullable = false)
    private Integer finalAmount;

    @Column(name = "cost_price", nullable = false)
    private Integer costPrice;

    @Column(name = "real_income", nullable = false)
    private Integer realIncome;

    @Column(name = "is_promo", nullable = false)
    private Integer isPromo;

    // DTO → Entity 변환 생성자
    public SalesDetailEntity(SalesDetailDTO dto) {
        this.salesDetailId = dto.getSalesDetailId();
        // transaction, product는 별도 매핑 필요
        this.salesQuantity = dto.getSalesQuantity();
        this.unitPrice = dto.getUnitPrice();
        this.discountPrice = dto.getDiscountPrice();
        this.finalAmount = dto.getFinalAmount();
        this.costPrice = dto.getCostPrice();
        this.realIncome = dto.getRealIncome();
        this.isPromo = dto.getIsPromo();
    }
}
