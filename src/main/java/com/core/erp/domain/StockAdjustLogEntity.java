package com.core.erp.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "stock_adjust_log")
@Getter
@Setter
public class StockAdjustLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer logId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private StoreEntity store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @Column(nullable = false)
    private int prevQuantity;

    @Column(nullable = false)
    private int newQuantity;

    @Column(length = 100, nullable = true)
    private String adjustReason;

    @Column(length = 50, nullable = false)
    private String adjustedBy;

    @Column(nullable = false)
    private LocalDateTime adjustDate;


}
