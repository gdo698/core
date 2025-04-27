package com.core.erp.dto;

import com.core.erp.domain.DashboardLayoutEntity;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DashboardLayoutDTO {

    private int layoutId;
    private Integer empId;
    private String dashWidgetCode;
    private String dashGridPositions;
    private LocalDateTime dashCreatedAt;
    private LocalDateTime dashUpdatedAt;

    private MultipartFile file;

    // Entity → DTO 변환 생성자
    public DashboardLayoutDTO(DashboardLayoutEntity entity) {
        this.layoutId = entity.getLayoutId();
        this.empId = entity.getEmployee() != null ? entity.getEmployee().getEmpId() : null;
        this.dashWidgetCode = entity.getDashWidgetCode();
        this.dashGridPositions = entity.getDashGridPositions();
        this.dashCreatedAt = entity.getDashCreatedAt();
        this.dashUpdatedAt = entity.getDashUpdatedAt();
    }
}