package com.core.erp.dto;

import com.core.erp.domain.ProductEntity;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProductDTO {

    private int productId;
    private Integer categoryId; // category FK (id만 관리)
    private String proName;
    private Long proBarcode;
    private int proCost;
    private int proSellCost;
    private LocalDateTime proCreatedAt;
    private LocalDateTime proUpdateAt;
    private String proImage;
    private Integer isPromo;

    private MultipartFile file;

    // Entity → DTO 변환 생성자
    public ProductDTO(ProductEntity entity) {
        this.productId = entity.getProductId();
        this.categoryId = entity.getCategory() != null ? entity.getCategory().getCategoryId() : null;
        this.proName = entity.getProName();
        this.proBarcode = entity.getProBarcode();
        this.proCost = entity.getProCost();
        this.proSellCost = entity.getProSellCost();
        this.proCreatedAt = entity.getProCreatedAt();
        this.proUpdateAt = entity.getProUpdateAt();
        this.proImage = entity.getProImage();
        this.isPromo = entity.getIsPromo();
    }
}
