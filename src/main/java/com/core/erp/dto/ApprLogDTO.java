package com.core.erp.dto;

import com.core.erp.domain.ApprLogEntity;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ApprLogDTO {

    private int logId;
    private Integer reqId; // FK (id만 관리)
    private Integer empId; // FK (id만 관리)
    private int apprStatus;
    private LocalDateTime apprAt;
    private String note;

    private MultipartFile file;

    // Entity → DTO 변환 생성자
    public ApprLogDTO(ApprLogEntity entity) {
        this.logId = entity.getLogId();
        this.reqId = entity.getLeaveReq() != null ? entity.getLeaveReq().getReqId() : null;
        this.empId = entity.getEmployee() != null ? entity.getEmployee().getEmpId() : null;
        this.apprStatus = entity.getApprStatus();
        this.apprAt = entity.getApprAt();
        this.note = entity.getNote();
    }
}