package com.core.erp.domain;

import com.core.erp.dto.product.ProductDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private int productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private CategoryEntity category;

    @Column(name = "pro_name", nullable = false, length = 255)
    private String proName;

    @Column(name = "pro_barcode", nullable = false)
    private Long proBarcode;

    @Column(name = "pro_cost", nullable = false)
    private int proCost;

    @Column(name = "pro_sell_cost", nullable = false)
    private int proSellCost;

    @Column(name = "pro_created_at", nullable = false)
    private LocalDateTime proCreatedAt;

    @Column(name = "pro_update_at")
    private LocalDateTime proUpdateAt;

    @Column(name = "pro_image", length = 225)
    private String proImage;

    @Column(name = "is_promo")
    private Integer isPromo;

    @Column(name = "pro_stock_limit")
    private int proStockLimit;

    @Column(name = "event_start")
    private LocalDateTime eventStart;

    @Column(name = "event_end")
    private LocalDateTime eventEnd;

    // 유통기한(입고일 기준 n일)
    @Column(name = "expiration_period", nullable = false)
    private int expirationPeriod;

    // DTO → Entity 변환 생성자
    public ProductEntity(ProductDTO dto) {
        // this.productId = dto.getProductId(); // 신규 등록 시 productId는 세팅하지 않음
        // category는 별도로 매핑 필요
        this.proName = dto.getProName();
        this.proBarcode = dto.getProBarcode();
        this.proCost = dto.getProCost();
        this.proSellCost = dto.getProSellCost();
        this.proCreatedAt = dto.getProCreatedAt();
        this.proUpdateAt = dto.getProUpdateAt();
        this.proImage = dto.getProImage();
        this.isPromo = dto.getIsPromo();
        this.proStockLimit = dto.getProStockLimit();
        this.expirationPeriod = dto.getExpirationPeriod();
    }
}
