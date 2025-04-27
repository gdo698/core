package com.core.erp.domain;

import com.core.erp.dto.StockInHistoryDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "stock_in_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StockInHistoryEntity {

    @Id
    @Column(name = "history_id")
    private int historyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private StoreEntity store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_timer_id", nullable = false)
    private PartTimerEntity partTimer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private PurchaseOrderEntity order;

    @Column(name = "in_quantity", nullable = false)
    private int inQuantity;

    @Column(name = "in_date", nullable = false)
    private LocalDateTime inDate;

    @Column(name = "expire_date")
    private LocalDateTime expireDate;

    @Column(name = "history_status", nullable = false)
    private int historyStatus;

    // DTO → Entity 변환 생성자
    public StockInHistoryEntity(StockInHistoryDTO dto) {
        this.historyId = dto.getHistoryId();
        // store, partTimer, product, order는 별도 매핑 필요
        this.inQuantity = dto.getInQuantity();
        this.inDate = dto.getInDate();
        this.expireDate = dto.getExpireDate();
        this.historyStatus = dto.getHistoryStatus();
    }
}