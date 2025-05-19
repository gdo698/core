package com.core.erp.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_transfer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockTransferEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transferId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    // 출발지 매장 (nullable: 창고 → 매장일 경우 null 가능)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_store_id")
    private StoreEntity fromStore;

    // 도착지 매장 (nullable: 매장 → 창고일 경우 null 가능)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_store_id")
    private StoreEntity toStore;

    /**
     * 이동 유형
     * 0 = 창고 → 매장
     * 1 = 매장 → 창고
     */

    @Column(nullable = false)
    private Integer transferType;

    @Column(nullable = false)
    private Integer quantity;

    private String reason;

    @Column(nullable = false, updatable = false)
    private LocalDateTime transferredAt = LocalDateTime.now();

    // 담당자 (선택)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transferred_by")
    private PartTimerEntity transferredBy;

}
