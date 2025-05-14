package com.core.erp.dto.sales;

import com.core.erp.domain.SalesDetailEntity;
import com.core.erp.dto.product.ProductDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SalesDetailDTO {

    private int salesDetailId ;
    private Integer transactionId; // FK (id만 관리)
    private Integer productId; // FK (id만 관리)
    private Integer  salesQuantity;
    private Integer  unitPrice;
    private Integer  discountPrice;
    private Integer  finalAmount;
    private Integer  costPrice;
    private Integer  realIncome;
    private Integer  isPromo;

    private Integer refundAmount;

    @JsonIgnore
    private ProductDTO product;
    private String productName;

    @JsonProperty("category")
    private String category;


    // Entity → DTO 변환 생성자
    public SalesDetailDTO(SalesDetailEntity entity) {
        this.salesDetailId = entity.getSalesDetailId();
        this.transactionId = entity.getTransaction() != null ? entity.getTransaction().getTransactionId() : null;
        this.productId = entity.getProduct() != null ? entity.getProduct().getProductId() : null;
        this.salesQuantity = entity.getSalesQuantity();
        this.unitPrice = entity.getUnitPrice();
        this.discountPrice = entity.getDiscountPrice();
        this.finalAmount = entity.getFinalAmount();
        this.costPrice = entity.getCostPrice();
        this.realIncome = entity.getRealIncome();
        this.isPromo = entity.getIsPromo();
        this.refundAmount = entity.getRefundAmount();

        if (entity.getProduct() != null) {
            this.productName = entity.getProduct().getProName();  // 상품명 세팅
            this.product = new ProductDTO(entity.getProduct());

            if (entity.getProduct().getCategory() != null) {
                this.category = entity.getProduct().getCategory().getCategoryName();  // 카테고리명 세팅
            }
        }
    }

}


