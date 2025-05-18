package com.core.erp.dto;

import com.core.erp.domain.AttendanceEntity;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AttendanceDTO {

    private int attendId;
    private Integer empId; // FK (nullable)
    private Integer leaveId; // FK (nullable)
    private Integer partTimerId; // FK (nullable)
    private Integer storeId; // FK (nullable)
    private LocalDateTime workDate;
    private LocalDateTime inTime;
    private LocalDateTime outTime;
    private int attendStatus;
    private String partName;
    private String position;


    // Entity → DTO 변환 생성자
    public AttendanceDTO(AttendanceEntity entity) {
        this.attendId = entity.getAttendId();
        this.empId = entity.getEmployee() != null ? entity.getEmployee().getEmpId() : null;
        this.leaveId = entity.getAnnualLeave() != null ? entity.getAnnualLeave().getLeaveId() : null;
        this.partTimerId = entity.getPartTimer() != null ? entity.getPartTimer().getPartTimerId() : null;
        this.storeId = entity.getStore() != null ? entity.getStore().getStoreId() : null;
        this.workDate = entity.getWorkDate();
        this.inTime = entity.getInTime();
        this.outTime = entity.getOutTime();
        this.attendStatus = entity.getAttendStatus();
        this.partName = entity.getPartTimer() != null ? entity.getPartTimer().getPartName() : null;
        this.position = entity.getPartTimer() != null ? entity.getPartTimer().getPosition() : null;
    }
}