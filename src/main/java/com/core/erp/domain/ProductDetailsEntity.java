package com.core.erp.domain;

import com.core.erp.dto.ProductDetailsDTO;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProductDetailsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pro_detail_id")
    private int proDetailId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @Column(name = "manufacturer", nullable = false, length = 100)
    private String manufacturer;

    @Column(name = "manu_num", length = 30)
    private String manuNum;

    @Column(name = "shelf_life", length = 50)
    private String shelfLife;

    @Column(name = "allergens", length = 255)
    private String allergens;

    @Column(name = "storage_method", length = 100)
    private String storageMethod;

    // DTO → Entity 변환 생성자
    public ProductDetailsEntity(ProductDetailsDTO dto) {
        this.proDetailId = dto.getProDetailId();
        // product는 별도 매핑 필요
        this.manufacturer = dto.getManufacturer();
        this.manuNum = dto.getManuNum();
        this.shelfLife = dto.getShelfLife();
        this.allergens = dto.getAllergens();
        this.storageMethod = dto.getStorageMethod();
    }
}