package com.core.erp.dto;

import com.core.erp.domain.AnnualLeaveEntity;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AnnualLeaveDTO {

    private int leaveId;
    private Integer empId; // FK (id만 관리)
    private int year;
    private int totalDays;
    private int usedDays;
    private Integer remDays;
    private LocalDateTime uadateAt;

    private MultipartFile file;

    // Entity → DTO 변환 생성자
    public AnnualLeaveDTO(AnnualLeaveEntity entity) {
        this.leaveId = entity.getLeaveId();
        this.empId = entity.getEmployee() != null ? entity.getEmployee().getEmpId() : null;
        this.year = entity.getYear();
        this.totalDays = entity.getTotalDays();
        this.usedDays = entity.getUsedDays();
        this.remDays = entity.getRemDays();
        this.uadateAt = entity.getUadateAt();
    }
}