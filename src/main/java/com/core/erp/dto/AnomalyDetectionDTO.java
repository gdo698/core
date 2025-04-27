package com.core.erp.dto;

import com.core.erp.domain.AnomalyDetectionEntity;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AnomalyDetectionDTO {

    private int anomalyId;
    private Integer storeId; // FK (id만 관리)
    private String anomType;
    private LocalDateTime anomDetectionTime;
    private int anomSeverity;
    private String anomDescription;
    private Integer anomIsResolved;
    private String anomResolutionNotes;
    private LocalDateTime anomCreatedAt;
    private LocalDateTime anomUpdatedAt;

    private MultipartFile file;

    // Entity → DTO 변환 생성자
    public AnomalyDetectionDTO(AnomalyDetectionEntity entity) {
        this.anomalyId = entity.getAnomalyId();
        this.storeId = entity.getStore() != null ? entity.getStore().getStoreId() : null;
        this.anomType = entity.getAnomType();
        this.anomDetectionTime = entity.getAnomDetectionTime();
        this.anomSeverity = entity.getAnomSeverity();
        this.anomDescription = entity.getAnomDescription();
        this.anomIsResolved = entity.getAnomIsResolved();
        this.anomResolutionNotes = entity.getAnomResolutionNotes();
        this.anomCreatedAt = entity.getAnomCreatedAt();
        this.anomUpdatedAt = entity.getAnomUpdatedAt();
    }
}