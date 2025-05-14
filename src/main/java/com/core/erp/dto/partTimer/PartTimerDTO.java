package com.core.erp.dto.partTimer;

import com.core.erp.domain.PartTimerEntity;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PartTimerDTO {

    private int partTimerId;
    private Integer storeId;
    private String partName;
    private int partGender;
    private String partPhone;
    private String partAddress;
    private LocalDate birthDate;
    private LocalDateTime hireDate;
    private LocalDateTime resignDate;
    private int salaryType;
    private Integer hourlyWage;
    private String accountBank;
    private String accountNumber;
    private int partStatus;
    private LocalDateTime createdAt;
    private String partImg;

    private MultipartFile file;

    private String position;
    private String workType;

    // Entity → DTO 변환 생성자
    public PartTimerDTO(PartTimerEntity entity) {
        this.partTimerId = entity.getPartTimerId();
        this.storeId = entity.getStore() != null ? entity.getStore().getStoreId() : null;
        this.partName = entity.getPartName();
        this.partGender = entity.getPartGender();
        this.partPhone = entity.getPartPhone();
        this.partAddress = entity.getPartAddress();
        this.birthDate = entity.getBirthDate();
        this.hireDate = entity.getHireDate();
        this.resignDate = entity.getResignDate();
        this.salaryType = entity.getSalaryType();
        this.hourlyWage = entity.getHourlyWage();
        this.accountBank = entity.getAccountBank();
        this.accountNumber = entity.getAccountNumber();
        this.partStatus = entity.getPartStatus();
        this.createdAt = entity.getCreatedAt();
        this.position = entity.getPosition();
        this.workType = entity.getWorkType();
        this.partImg = entity.getPartImg();

    }
}