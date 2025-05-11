package com.core.erp.dto;

import com.core.erp.domain.StockInHistoryEntity;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StockInHistoryDTO {

    private int historyId;
    private Integer storeId;
    private Integer partTimerId;
    private Integer productId;
    private Long orderId;;
    private int inQuantity;
    private LocalDateTime inDate;
    private LocalDateTime expireDate;
    private int historyStatus;

    private MultipartFile file;

    // Entity → DTO 변환 생성자
    public StockInHistoryDTO(StockInHistoryEntity entity) {
        this.historyId = entity.getHistoryId();
        this.storeId = entity.getStore() != null ? entity.getStore().getStoreId() : null;
        this.partTimerId = entity.getPartTimer() != null ? entity.getPartTimer().getPartTimerId() : null;
        this.productId = entity.getProduct() != null ? entity.getProduct().getProductId() : null;
        this.orderId = entity.getOrder() != null ? entity.getOrder().getOrderId() : null;
        this.inQuantity = entity.getInQuantity();
        this.inDate = entity.getInDate();
        this.expireDate = entity.getExpireDate();
        this.historyStatus = entity.getHistoryStatus();
    }
}