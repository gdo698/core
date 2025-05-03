package com.core.erp.domain;

import com.core.erp.dto.AiModelDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ai_model")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AiModelEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "model_id")
    private int modelId;

    @Column(name = "ai_name", nullable = false, length = 100)
    private String aiName;

    @Column(name = "ai_type", nullable = false, length = 50)
    private String aiType;

    @Column(name = "ai_version", nullable = false, length = 20)
    private String aiVersion;

    @Column(name = "ai_parameters", columnDefinition = "json")
    private String aiParameters;

    @Column(name = "ai_accuracy")
    private Double aiAccuracy;

    @Column(name = "ai_training_date")
    private LocalDateTime aiTrainingDate;

    @Column(name = "ai_is_active")
    private Boolean aiIsActive;

    @Column(name = "ai_created_at")
    private LocalDateTime aiCreatedAt;

    @Column(name = "ai_updated_at")
    private LocalDateTime aiUpdatedAt;

    // DTO → Entity 변환 생성자
    public AiModelEntity(AiModelDTO dto) {
        this.modelId = dto.getModelId();
        this.aiName = dto.getAiName();
        this.aiType = dto.getAiType();
        this.aiVersion = dto.getAiVersion();
        this.aiParameters = dto.getAiParameters();
        this.aiAccuracy = dto.getAiAccuracy();
        this.aiTrainingDate = dto.getAiTrainingDate();
        this.aiIsActive = dto.getAiIsActive();
        this.aiCreatedAt = dto.getAiCreatedAt();
        this.aiUpdatedAt = dto.getAiUpdatedAt();
    }
}
