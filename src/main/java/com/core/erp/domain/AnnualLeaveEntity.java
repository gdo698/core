package com.core.erp.domain;

import com.core.erp.dto.AnnualLeaveDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "annual_leave")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AnnualLeaveEntity {

    @Id
    @Column(name = "leave_id")
    private int leaveId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emp_id", nullable = false)
    private EmployeeEntity employee;

    @Column(name = "year", nullable = false)
    private int year;

    @Column(name = "total_days", nullable = false)
    private int totalDays;

    @Column(name = "used_days", nullable = false)
    private int usedDays;

    @Column(name = "rem_days")
    private Integer remDays;

    @Column(name = "uadate_at")
    private LocalDateTime uadateAt;

    // DTO → Entity 변환 생성자
    public AnnualLeaveEntity(AnnualLeaveDTO dto) {
        this.leaveId = dto.getLeaveId();
        // employee는 별도 매핑 필요
        this.year = dto.getYear();
        this.totalDays = dto.getTotalDays();
        this.usedDays = dto.getUsedDays();
        this.remDays = dto.getRemDays();
        this.uadateAt = dto.getUadateAt();
    }
}