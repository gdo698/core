package com.core.erp.domain;

import com.core.erp.dto.ApprLogDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "appr_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ApprLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private int logId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "req_id", nullable = false)
    private LeaveReqEntity leaveReq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emp_id", nullable = false)
    private EmployeeEntity employee;

    @Column(name = "appr_status", nullable = false)
    private int apprStatus;

    @Column(name = "appr_at", nullable = false)
    private LocalDateTime apprAt;

    @Column(name = "note", length = 255)
    private String note;

    // DTO → Entity 변환 생성자
    public ApprLogEntity(ApprLogDTO dto) {
        this.logId = dto.getLogId();
        // leaveReq, employee는 별도 매핑 필요
        this.apprStatus = dto.getApprStatus();
        this.apprAt = dto.getApprAt();
        this.note = dto.getNote();
    }
}