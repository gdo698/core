package com.core.erp.dto;

import com.core.erp.domain.SalaryEntity;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SalaryDTO {

    private int salaryId;
    private Integer empId; // FK (nullable)
    private Integer partTimerId; // FK (nullable)
    private Integer storeId; // FK (nullable)
    private LocalDateTime calculatedAt;
    private int baseSalary;
    private int bonus;
    private int deductTotal;
    private Integer deductExtra;
    private int netSalary;
    private LocalDateTime payDate;
    private int payStatus;

    private MultipartFile file;

    // Entity → DTO 변환 생성자
    public SalaryDTO(SalaryEntity entity) {
        this.salaryId = entity.getSalaryId();
        this.empId = entity.getEmployee() != null ? entity.getEmployee().getEmpId() : null;
        this.partTimerId = entity.getPartTimer() != null ? entity.getPartTimer().getPartTimerId() : null;
        this.storeId = entity.getStore() != null ? entity.getStore().getStoreId() : null;
        this.calculatedAt = entity.getCalculatedAt();
        this.baseSalary = entity.getBaseSalary();
        this.bonus = entity.getBonus();
        this.deductTotal = entity.getDeductTotal();
        this.deductExtra = entity.getDeductExtra();
        this.netSalary = entity.getNetSalary();
        this.payDate = entity.getPayDate();
        this.payStatus = entity.getPayStatus();
    }
}