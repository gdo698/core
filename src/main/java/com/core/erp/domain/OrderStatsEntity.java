package com.core.erp.domain;

import com.core.erp.dto.OrderStatsDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_stats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderStatsEntity {

    @Id
    @Column(name = "ostats_id")
    private int ostatsId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private StoreEntity store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @Column(name = "ostats_date", nullable = false)
    private LocalDate ostatsDate;

    @Column(name = "ostats_quantity", nullable = false)
    private int ostatsQuantity;

    @Column(name = "ostats_total", nullable = false)
    private int ostatsTotal;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // DTO → Entity 변환 생성자
    public OrderStatsEntity(OrderStatsDTO dto) {
        this.ostatsId = dto.getOstatsId();
        // store, product는 별도 매핑 필요
        this.ostatsDate = dto.getOstatsDate();
        this.ostatsQuantity = dto.getOstatsQuantity();
        this.ostatsTotal = dto.getOstatsTotal();
        this.createdAt = dto.getCreatedAt();
    }
}
