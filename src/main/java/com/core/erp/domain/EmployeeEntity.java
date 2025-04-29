package com.core.erp.domain;

import com.core.erp.dto.EmployeeDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "employee")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EmployeeEntity {

    @Id
    @Column(name = "emp_id")
    private int empId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private StoreEntity store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "depart_id")
    private DepartmentEntity department;

    @Column(name = "emp_name", nullable = false, length = 30)
    private String empName;

    @Column(name = "emp_role", nullable = false, length = 30)
    private String empRole;

    @Column(name = "emp_gender", nullable = false)
    private int empGender;

    @Column(name = "emp_phone", nullable = false, length = 30)
    private String empPhone;

    @Column(name = "emp_addr", nullable = false, length = 30)
    private String empAddr;

    @Column(name = "emp_birth", nullable = false, length = 30)
    private String empBirth;

    @Column(name = "login_id", nullable = false, unique = true, length = 30)
    private String loginId;

    @Column(name = "login_pwd", nullable = false, length = 30)
    private String loginPwd;

    @Column(name = "emp_img", length = 255)
    private String empImg;

    @Column(name = "emp_bank", nullable = false)
    private int empBank;

    @Column(name = "emp_acount", nullable = false, length = 30)
    private String empAcount;

    @Column(name = "emp_status", nullable = false, length = 30)
    private String empStatus;

    @Column(name = "hire_date", nullable = false)
    private LocalDateTime hireDate;

    @Column(name = "work_type", nullable = false)
    private int workType;

    @Column(name = "email_auth")
    private Integer emailAuth;

    @Column(name = "emp_ext")
    private Integer empExt;

    // DTO → Entity 변환 생성자
    public EmployeeEntity(EmployeeDTO dto) {
        this.empId = dto.getEmpId();
        // store, department는 따로 매핑 필요
        this.empName = dto.getEmpName();
        this.empRole = dto.getEmpRole();
        this.empGender = dto.getEmpGender();
        this.empPhone = dto.getEmpPhone();
        this.empAddr = dto.getEmpAddr();
        this.empBirth = dto.getEmpBirth();
        this.loginId = dto.getLoginId();
        this.loginPwd = dto.getLoginPwd();
        this.empImg = dto.getEmpImg();
        this.empBank = dto.getEmpBank();
        this.empAcount = dto.getEmpAcount();
        this.empStatus = dto.getEmpStatus();
        this.hireDate = dto.getHireDate();
        this.workType = dto.getWorkType();
        this.emailAuth = dto.getEmailAuth();
        this.empExt = dto.getEmpExt();
    }

}