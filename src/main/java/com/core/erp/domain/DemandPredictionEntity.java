package com.core.erp.domain;

import com.core.erp.dto.DemandPredictionDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "demand_prediction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DemandPredictionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prediction_id")
    private int predictionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private StoreEntity store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @Column(name = "dmd_date", nullable = false)
    private LocalDate dmdDate;

    @Column(name = "dmd_predicted_quantity", nullable = false)
    private int dmdPredictedQuantity;

    @Column(name = "dmd_confidence_level", nullable = false)
    private Double dmdConfidenceLevel;

    @Column(name = "dmd_weather_factor")
    private Double dmdWeatherFactor;

    @Column(name = "dmd_seasonal_factor")
    private Double dmdSeasonalFactor;

    @Column(name = "dmd_created_at")
    private LocalDateTime dmdCreatedAt;

    // DTO → Entity 변환 생성자
    public DemandPredictionEntity(DemandPredictionDTO dto) {
        this.predictionId = dto.getPredictionId();
        // store, product는 별도 매핑 필요
        this.dmdDate = dto.getDmdDate();
        this.dmdPredictedQuantity = dto.getDmdPredictedQuantity();
        this.dmdConfidenceLevel = dto.getDmdConfidenceLevel();
        this.dmdWeatherFactor = dto.getDmdWeatherFactor();
        this.dmdSeasonalFactor = dto.getDmdSeasonalFactor();
        this.dmdCreatedAt = dto.getDmdCreatedAt();
    }
}
