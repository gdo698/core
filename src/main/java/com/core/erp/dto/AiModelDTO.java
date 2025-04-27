package com.core.erp.dto;

import com.core.erp.domain.AiModelEntity;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AiModelDTO {

    private int modelId;
    private String aiName;
    private String aiType;
    private String aiVersion;
    private String aiParameters;
    private Double aiAccuracy;
    private LocalDateTime aiTrainingDate;
    private Boolean aiIsActive;
    private LocalDateTime aiCreatedAt;
    private LocalDateTime aiUpdatedAt;

    private MultipartFile file;

    // Entity → DTO 변환 생성자
    public AiModelDTO(AiModelEntity entity) {
        this.modelId = entity.getModelId();
        this.aiName = entity.getAiName();
        this.aiType = entity.getAiType();
        this.aiVersion = entity.getAiVersion();
        this.aiParameters = entity.getAiParameters();
        this.aiAccuracy = entity.getAiAccuracy();
        this.aiTrainingDate = entity.getAiTrainingDate();
        this.aiIsActive = entity.getAiIsActive();
        this.aiCreatedAt = entity.getAiCreatedAt();
        this.aiUpdatedAt = entity.getAiUpdatedAt();
    }
}