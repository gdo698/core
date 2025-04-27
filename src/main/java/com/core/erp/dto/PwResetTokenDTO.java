package com.core.erp.dto;

import com.core.erp.domain.PwResetTokenEntity;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PwResetTokenDTO {

    private int prtokenId;
    private Integer empId; // FK (id만 관리)
    private String resetToken;
    private LocalDateTime prtokenExp;
    private boolean prtokenUsed;
    private LocalDateTime createdAt;

    // Entity → DTO 변환 생성자
    public PwResetTokenDTO(PwResetTokenEntity entity) {
        this.prtokenId = entity.getPrtokenId();
        this.empId = entity.getEmployee() != null ? entity.getEmployee().getEmpId() : null;
        this.resetToken = entity.getResetToken();
        this.prtokenExp = entity.getPrtokenExp();
        this.prtokenUsed = entity.isPrtokenUsed();
        this.createdAt = entity.getCreatedAt();
    }
}