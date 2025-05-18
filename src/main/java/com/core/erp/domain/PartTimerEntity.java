package com.core.erp.domain;

import com.core.erp.dto.partTimer.PartTimerDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "part_timer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PartTimerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "part_timer_id")
    private int partTimerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private StoreEntity store;

    @Column(name = "position", nullable = false, length = 50)
    private String position;

    @Column(name = "work_type", nullable = false, length = 50)
    private String workType;

    @Column(name = "part_name", nullable = false, length = 50)
    private String partName;

    @Column(name = "part_gender", nullable = false)
    private int partGender;

    @Column(name = "part_phone", nullable = false, length = 30)
    private String partPhone;

    @Column(name = "part_addres", nullable = false, length = 50)
    private String partAddress;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "hire_date", nullable = false)
    private LocalDateTime hireDate;

    @Column(name = "resign_date")
    private LocalDateTime resignDate;

    @Column(name = "salary_type", nullable = false)
    private int salaryType;

    @Column(name = "hourly_wage")
    private Integer hourlyWage;

    @Column(name = "account_bank", nullable = false, length = 30)
    private String accountBank;

    @Column(name = "account_number", nullable = false, length = 30)
    private String accountNumber;

    @Column(name = "part_status", nullable = false)
    private int partStatus;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "part_img", length = 200)
    private String partImg;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // DTO → Entity 변환 생성자
    public PartTimerEntity(PartTimerDTO dto, StoreEntity store) {
        this.partTimerId = dto.getPartTimerId();
        this.store = store;
        this.position = dto.getPosition();
        this.workType = dto.getWorkType();
        this.partName = dto.getPartName();
        this.partGender = dto.getPartGender();
        this.partPhone = dto.getPartPhone();
        this.partAddress = dto.getPartAddress();
        this.birthDate = dto.getBirthDate();
        this.hireDate = dto.getHireDate();
        this.resignDate = dto.getResignDate();
        this.salaryType = dto.getSalaryType();
        this.hourlyWage = dto.getHourlyWage();
        this.accountBank = dto.getAccountBank();
        this.accountNumber = dto.getAccountNumber();
        this.partStatus = dto.getPartStatus();
        this.createdAt = dto.getCreatedAt();
    }

    public PartTimerDTO toDTO() {
        return new PartTimerDTO(this);
    }

    // ID만 받는 생성자 (정산용)
    public PartTimerEntity(Integer partTimerId) {
        this.partTimerId = partTimerId;
    }

}