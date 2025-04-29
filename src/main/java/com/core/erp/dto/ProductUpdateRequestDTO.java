package com.core.erp.dto;

import lombok.*;
import java.time.LocalDateTime;
import org.springframework.web.multipart.MultipartFile;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ProductUpdateRequestDTO {
    // 상품 정보
    private int productId;
    private String proName;
    private Integer proStockLimit;
    private Integer proCost;
    private Integer proSellCost;
    private Integer isPromo; // 0:판매중, 1:단종, 2:1+1, 3:2+1
    private String proImage; // 이미지 URL
    private String eventStart; // yyyy-MM-dd
    private String eventEnd;   // yyyy-MM-dd

    // 부가 정보
    private String manufacturer;
    private String manuNum;
    private String shelfLife;
    private String allergens;
    private String storageMethod;
    private Integer categoryId;
}