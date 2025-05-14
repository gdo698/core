package com.core.erp.domain;

import com.core.erp.dto.employee.DepartmentDTO;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "department")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DepartmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dept_id")
    private int deptId;

    @Column(name = "dept_name", nullable = false, length = 30)
    private String deptName;

    // DTO → Entity 변환 생성자
    public DepartmentEntity(DepartmentDTO dto) {
        this.deptId = dto.getDeptId();
        this.deptName = dto.getDeptName();
    }
}