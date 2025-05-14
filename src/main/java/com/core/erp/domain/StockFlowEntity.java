package com.core.erp.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "stock_flow")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockFlowEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "flow_id")
    private Long flowId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private StoreEntity store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    /**
     * 재고 흐름 유형 (TINYINT)
     * 0: 입고 (IN)
     * 1: 출고 (OUT)
     * 2: 판매 (SALE)
     * 3: 폐기 (DISPOSAL)
     * 4: 조정 (ADJUST)
     * 5: 반품 (RETURN)
     * 6: 이동출고 (MOVE_OUT)
     * 7: 이동입고 (MOVE_IN)
     */

    @Column(name = "flow_type", nullable = false)
    private Integer flowType;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "before_quantity", nullable = false)
    private int beforeQuantity;

    @Column(name = "after_quantity", nullable = false)
    private int afterQuantity;

    @Column(length = 30)
    private String location; // "매장", "창고"

    @Column(length = 255)
    private String note; // 비고, 사유 등

    @Column(name = "processed_by", length = 50)
    private String processedBy;

    @Column(name = "flow_date", nullable = false)
    private LocalDateTime flowDate;
}
