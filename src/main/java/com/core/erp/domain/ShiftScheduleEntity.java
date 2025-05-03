package com.core.erp.domain;

import com.core.erp.dto.ShiftScheduleDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "shift_schedule")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ShiftScheduleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private int scheduleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_timer_id", nullable = false)
    private PartTimerEntity partTimer;

    @Column(name = "work_date", nullable = false)
    private LocalDateTime workDate;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    // DTO → Entity 변환 생성자
    public ShiftScheduleEntity(ShiftScheduleDTO dto) {
        this.scheduleId = dto.getScheduleId();
        // partTimer는 별도 매핑 필요
        this.workDate = dto.getWorkDate();
        this.startTime = dto.getStartTime();
        this.endTime = dto.getEndTime();
    }
}