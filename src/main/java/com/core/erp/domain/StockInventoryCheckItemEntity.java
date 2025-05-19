package com.core.erp.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Formula;

import java.util.Optional;

@Entity
@Table(name = "stock_inventory_check_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockInventoryCheckItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long checkItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "check_id")
    private StockInventoryCheckEntity inventoryCheck;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    @Column(nullable = false)
    private Integer storePrevQuantity;

    @Column(nullable = false)
    private Integer warehousePrevQuantity;

    @Column(nullable = false)
    private Integer storeRealQuantity;

    @Column(nullable = false)
    private Integer warehouseRealQuantity;

    @Formula("store_real_quantity - store_prev_quantity")
    private Integer storeDifference;

    @Formula("warehouse_real_quantity - warehouse_prev_quantity")
    private Integer warehouseDifference;

    @Formula("(store_real_quantity + warehouse_real_quantity) - (store_prev_quantity + warehouse_prev_quantity)")
    private Integer totalDifference;

    @Column(name = "is_applied", nullable = false)
    private Boolean isApplied = false;

}
