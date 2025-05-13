package com.core.erp.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Formula;

import java.time.LocalDateTime;

@Entity
@Table(name = "stock_inventory_check")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockInventoryCheckEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer checkId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private StoreEntity store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_timer_id", nullable = false)
    private PartTimerEntity partTimer;

    @Column(nullable = false)
    private Integer prevQuantity;

    @Column(nullable = false)
    private Integer realQuantity;

    private String checkReason;

    @Formula("real_quantity - prev_quantity")
    private Integer difference;

    @Column(nullable = false, updatable = false)
    private LocalDateTime checkDate = LocalDateTime.now();

    @Column(nullable = false)
    private Boolean isApplied = false;

}
