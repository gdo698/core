package com.core.erp.dto;

import com.core.erp.domain.ProductDetailsEntity;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProductDetailsDTO {

    private int proDetailId;
    private Integer productId; // FK
    private String manufacturer;
    private String manuNum;
    private String shelfLife;
    private String allergens;
    private String storageMethod;

    private MultipartFile file;

    // Entity → DTO 변환 생성자
    public ProductDetailsDTO(ProductDetailsEntity entity) {
        this.proDetailId = entity.getProDetailId();
        this.productId = entity.getProduct() != null ? entity.getProduct().getProductId() : null;
        this.manufacturer = entity.getManufacturer();
        this.manuNum = entity.getManuNum();
        this.shelfLife = entity.getShelfLife();
        this.allergens = entity.getAllergens();
        this.storageMethod = entity.getStorageMethod();
    }
}