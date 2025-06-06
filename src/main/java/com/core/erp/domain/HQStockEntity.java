package com.core.erp.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "hq_stock")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class HQStockEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hq_stock_id")
    private int hqStockId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @Column(name = "quantity", nullable = false)
    private int quantity;  // 본사 재고 수량
    
    @Column(name = "total_quantity", nullable = false)
    private int totalQuantity;  // 총 재고 수량 (본사 + 매장)

    @Column(name = "regular_in_day")
    private Integer regularInDay;  // 정기 입고일 (1~30)

    @Column(name = "regular_in_quantity")
    private Integer regularInQuantity;  // 정기 입고 수량

    @Column(name = "regular_in_active")
    private Boolean regularInActive = false;  // 정기 입고 활성화 여부 (기본값: 비활성화)

    @Column(name = "last_update")
    private LocalDateTime lastUpdate;
    
    // 로깅을 위한 변경 내역
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_by")
    private String updatedBy;
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.lastUpdate = LocalDateTime.now();
    }
}