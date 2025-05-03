package com.core.erp.domain;

import com.core.erp.dto.AnomalyDetectionDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "anomaly_detection")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AnomalyDetectionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "anomaly_id")
    private int anomalyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private StoreEntity store;

    @Column(name = "anom_type", nullable = false, length = 50)
    private String anomType;

    @Column(name = "anom_detection_time", nullable = false)
    private LocalDateTime anomDetectionTime;

    @Column(name = "anom_severity", nullable = false)
    private int anomSeverity;

    @Column(name = "anom_description", nullable = false, columnDefinition = "TEXT")
    private String anomDescription;

    @Column(name = "anom_is_resolved")
    private int anomIsResolved;

    @Column(name = "anom_resolution_notes", columnDefinition = "TEXT")
    private String anomResolutionNotes;

    @Column(name = "anom_created_at")
    private LocalDateTime anomCreatedAt;

    @Column(name = "anom_updated_at")
    private LocalDateTime anomUpdatedAt;

    // DTO → Entity 변환 생성자
    public AnomalyDetectionEntity(AnomalyDetectionDTO dto) {
        this.anomalyId = dto.getAnomalyId();
        // store는 별도 매핑 필요
        this.anomType = dto.getAnomType();
        this.anomDetectionTime = dto.getAnomDetectionTime();
        this.anomSeverity = dto.getAnomSeverity();
        this.anomDescription = dto.getAnomDescription();
        this.anomIsResolved = dto.getAnomIsResolved();
        this.anomResolutionNotes = dto.getAnomResolutionNotes();
        this.anomCreatedAt = dto.getAnomCreatedAt();
        this.anomUpdatedAt = dto.getAnomUpdatedAt();
    }
}
