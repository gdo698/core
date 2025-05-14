package com.core.erp.dto.employee;

import com.core.erp.domain.EmployeeEntity;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EmployeeDTO {

    private int empId;
    private Integer storeId; // store FK (id만 관리)
    private Integer departId; // department FK (id만 관리)
    private String empName;
    private String empRole;
    private int empGender;
    private String empPhone;
    private String empAddr;
    private String empBirth;
    private String loginId;
    private String loginPwd;
    private String empImg;
    private int empBank;
    private String empAcount;
    private String empStatus;
    private LocalDateTime hireDate;
    private int workType;
    private Integer emailAuth;
    private Integer empExt;

    private MultipartFile file;

    // Entity → DTO 변환 생성자
    public EmployeeDTO(EmployeeEntity entity) {
        this.empId = entity.getEmpId();
        this.storeId = entity.getStore() != null ? entity.getStore().getStoreId() : null;
        this.departId = entity.getDepartment() != null ? entity.getDepartment().getDeptId() : null;
        this.empName = entity.getEmpName();
        this.empRole = entity.getEmpRole();
        this.empGender = entity.getEmpGender();
        this.empPhone = entity.getEmpPhone();
        this.empAddr = entity.getEmpAddr();
        this.empBirth = entity.getEmpBirth();
        this.loginId = entity.getLoginId();
        this.loginPwd = entity.getLoginPwd();
        this.empImg = entity.getEmpImg();
        this.empBank = entity.getEmpBank();
        this.empAcount = entity.getEmpAcount();
        this.empStatus = entity.getEmpStatus();
        this.hireDate = entity.getHireDate();
        this.workType = entity.getWorkType();
        this.emailAuth = entity.getEmailAuth();
        this.empExt = entity.getEmpExt();
    }
}