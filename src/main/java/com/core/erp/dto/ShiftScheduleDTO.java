package com.core.erp.dto;

import com.core.erp.domain.ShiftScheduleEntity;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ShiftScheduleDTO {

    private int scheduleId;
    private Integer partTimerId; // FK (id만 관리)
    private LocalDateTime workDate;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private MultipartFile file;

    // Entity → DTO 변환 생성자
    public ShiftScheduleDTO(ShiftScheduleEntity entity) {
        this.scheduleId = entity.getScheduleId();
        this.partTimerId = entity.getPartTimer() != null ? entity.getPartTimer().getPartTimerId() : null;
        this.workDate = entity.getWorkDate();
        this.startTime = entity.getStartTime();
        this.endTime = entity.getEndTime();
    }
}