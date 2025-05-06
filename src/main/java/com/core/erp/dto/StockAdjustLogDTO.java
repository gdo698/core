package com.core.erp.dto;

import com.core.erp.domain.StockAdjustLogEntity;
import lombok.*;

import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StockAdjustLogDTO {

    private Integer logId;
    private String storeName;
    private String productName;
    private int prevQuantity;
    private int newQuantity;
    private String adjustReason;
    private String adjustedBy;
    private String adjustDateFormatted;

    // Entity → DTO 변환 생성자
    public StockAdjustLogDTO(StockAdjustLogEntity entity) {
        this.logId = entity.getLogId();
        this.storeName = entity.getStore().getStoreName();
        this.productName = entity.getProduct().getProName();
        this.prevQuantity = entity.getPrevQuantity();
        this.newQuantity = entity.getNewQuantity();
        this.adjustReason = entity.getAdjustReason();
        this.adjustedBy = entity.getAdjustedBy();
        this.adjustDateFormatted = entity.getAdjustDate()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
}
