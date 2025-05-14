package com.core.erp.domain;

import com.core.erp.dto.disposal.DisposalDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "disposal")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DisposalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "disposal_id")
    private int disposalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", nullable = false)
    private StoreStockEntity storeStock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)  // product_id와 연결
    private ProductEntity product;  // ProductEntity와 연결

    @Column(name = "disposal_date", nullable = false)
    private LocalDateTime disposalDate;

    @Column(name = "disposal_quantity", nullable = false)
    private int disposalQuantity;

    @Column(name = "processed_by", nullable = false, length = 30)
    private String processedBy;

    @Column(name = "total_loss_amount", nullable = false)
    private int totalLossAmount;

    @Column(name = "disposal_reason", nullable = false, length = 30)
    private String disposalReason;

    @Column(name = "pro_name", nullable = false, length = 255)
    private String proName;

    // DTO → Entity 변환 생성자
    public DisposalEntity(DisposalDTO dto) {
        this.disposalId = dto.getDisposalId();
        // storeStock은 별도 매핑 필요
        this.storeStock = new StoreStockEntity();
        this.product = new ProductEntity();
        this.product.setProductId(dto.getProductId());
        this.proName = dto.getProName();
        this.disposalDate = dto.getDisposalDate();
        this.disposalQuantity = dto.getDisposalQuantity();
        this.processedBy = dto.getProcessedBy();
        this.totalLossAmount = dto.getTotalLossAmount();
        this.disposalReason = dto.getDisposalReason();
    }
}
