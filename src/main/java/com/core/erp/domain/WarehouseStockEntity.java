package com.core.erp.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "warehouse_stock")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class WarehouseStockEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int stockId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @Column(name = "warehouse_id", nullable = false)
    private int warehouseId;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "last_in_date", nullable = false)
    private LocalDateTime lastInDate;

    @Column(name = "stock_status", nullable = false)
    private int stockStatus;


}
