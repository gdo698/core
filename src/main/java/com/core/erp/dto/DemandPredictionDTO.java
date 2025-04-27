package com.core.erp.dto;

import com.core.erp.domain.DemandPredictionEntity;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DemandPredictionDTO {

    private int predictionId;
    private Integer storeId; // FK (id만 관리)
    private Integer productId; // FK (id만 관리)
    private LocalDate dmdDate;
    private int dmdPredictedQuantity;
    private Double dmdConfidenceLevel;
    private Double dmdWeatherFactor;
    private Double dmdSeasonalFactor;
    private LocalDateTime dmdCreatedAt;

    private MultipartFile file;

    // Entity → DTO 변환 생성자
    public DemandPredictionDTO(DemandPredictionEntity entity) {
        this.predictionId = entity.getPredictionId();
        this.storeId = entity.getStore() != null ? entity.getStore().getStoreId() : null;
        this.productId = entity.getProduct() != null ? entity.getProduct().getProductId() : null;
        this.dmdDate = entity.getDmdDate();
        this.dmdPredictedQuantity = entity.getDmdPredictedQuantity();
        this.dmdConfidenceLevel = entity.getDmdConfidenceLevel();
        this.dmdWeatherFactor = entity.getDmdWeatherFactor();
        this.dmdSeasonalFactor = entity.getDmdSeasonalFactor();
        this.dmdCreatedAt = entity.getDmdCreatedAt();
    }
}