package com.core.erp.dto;

import com.core.erp.domain.SalesEntity;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SalesDTO {

    private int salesId;
    private Integer storeId; // FK (id만 관리)
    private Integer empId; // FK (id만 관리)
    private Integer productId; // FK (id만 관리)
    private int salesTotal;
    private String paymentMethod;
    private LocalDateTime salesTime;
    private int salesQuantity;
    private int isRefunded;
    private int discountPrice;
    private LocalDateTime createdAt;
    private int finalAmount;
    private int costPrice;
    private int realIncome;
    private int isSettled;
    private Integer transactionId;

    private MultipartFile file;

    // Entity → DTO 변환 생성자
    public SalesDTO(SalesEntity entity) {
        this.salesId = entity.getSalesId();
        this.storeId = entity.getStore() != null ? entity.getStore().getStoreId() : null;
        this.empId = entity.getEmployee() != null ? entity.getEmployee().getEmpId() : null;
        this.productId = entity.getProduct() != null ? entity.getProduct().getProductId() : null;
        this.salesTotal = entity.getSalesTotal();
        this.paymentMethod = entity.getPaymentMethod();
        this.salesTime = entity.getSalesTime();
        this.salesQuantity = entity.getSalesQuantity();
        this.isRefunded = entity.getIsRefunded();
        this.discountPrice = entity.getDiscountPrice();
        this.createdAt = entity.getCreatedAt();
        this.finalAmount = entity.getFinalAmount();
        this.costPrice = entity.getCostPrice();
        this.realIncome = entity.getRealIncome();
        this.isSettled = entity.getIsSettled();
        this.transactionId = entity.getTransactionId();
    }
}