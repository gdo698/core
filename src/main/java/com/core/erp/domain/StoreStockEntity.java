package com.core.erp.domain;

import com.core.erp.dto.StoreStockDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "store_stock")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StoreStockEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_id")
    private int stockId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private StoreEntity store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "last_in_date")
    private LocalDateTime lastInDate;

    @Column(name = "stock_status", nullable = false)
    private int stockStatus;

    // DTO → Entity 변환 생성자
    public StoreStockEntity(StoreStockDTO dto) {
        this.stockId = dto.getStockId();
        // store, product는 따로 매핑 필요
        this.quantity = dto.getQuantity();
        this.lastInDate = dto.getLastInDate();
        this.stockStatus = dto.getStockStatus();
    }

    public StoreStockEntity(Integer o, StoreEntity store, ProductEntity product, int quantity, Object lastInDate, int stockStatus) {
        this.store = store;
        this.product = product;
        this.quantity = quantity;
        if (lastInDate instanceof LocalDateTime) {
            this.lastInDate = (LocalDateTime) lastInDate;
        } else {
            this.lastInDate = LocalDateTime.now();
        }
        this.stockStatus = stockStatus;
    }
}