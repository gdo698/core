package com.core.erp.dto;

import com.core.erp.domain.EmailTokenEntity;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EmailTokenDTO {

    private int etokenId;
    private Integer empId; // FK (id만 관리)
    private String etoken;
    private LocalDateTime etokenExp;
    private boolean etokenUsed;
    private LocalDateTime createdAt;

    private MultipartFile file;

    // Entity → DTO 변환 생성자
    public EmailTokenDTO(EmailTokenEntity entity) {
        this.etokenId = entity.getEtokenId();
        this.empId = entity.getEmployee() != null ? entity.getEmployee().getEmpId() : null;
        this.etoken = entity.getEtoken();
        this.etokenExp = entity.getEtokenExp();
        this.etokenUsed = entity.isEtokenUsed();
        this.createdAt = entity.getCreatedAt();
    }
}