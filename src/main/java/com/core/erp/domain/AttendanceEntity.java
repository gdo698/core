package com.core.erp.domain;

import com.core.erp.dto.AttendanceDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "attendance")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AttendanceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attend_id")
    private int attendId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emp_id")
    private EmployeeEntity employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_id")
    private AnnualLeaveEntity annualLeave;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_timer_id")
    private PartTimerEntity partTimer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private StoreEntity store;

    @Column(name = "work_date", nullable = false)
    private LocalDateTime workDate;

    @Column(name = "in_time", nullable = false)
    private LocalDateTime inTime;

    @Column(name = "out_time")
    private LocalDateTime outTime;

    @Column(name = "attend_status", nullable = false)
    private int attendStatus;

    // DTO → Entity 변환 생성자
    public AttendanceEntity(AttendanceDTO dto) {
        this.attendId = dto.getAttendId();
        // employee, annualLeave, partTimer, store는 별도 매핑 필요
        this.workDate = dto.getWorkDate();
        this.inTime = dto.getInTime();
        this.outTime = dto.getOutTime();
        this.attendStatus = dto.getAttendStatus();
    }
}