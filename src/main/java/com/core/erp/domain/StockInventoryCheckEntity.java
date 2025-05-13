package com.core.erp.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Formula;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private Long checkId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private StoreEntity store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_timer_id")
    private PartTimerEntity partTimer;

    @Column(nullable = false)
    private String checkReason;

    @Column(nullable = false, updatable = false)
    private LocalDateTime checkDate = LocalDateTime.now();

    @Column(nullable = false)
    private Boolean isApplied = false;

    @OneToMany(mappedBy = "inventoryCheck", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StockInventoryCheckItemEntity> items = new ArrayList<>();

}