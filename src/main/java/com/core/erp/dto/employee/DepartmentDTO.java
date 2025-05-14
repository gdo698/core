package com.core.erp.dto.employee;

import com.core.erp.domain.DepartmentEntity;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DepartmentDTO {

    private int deptId;
    private String deptName;

    private MultipartFile file;

    // Entity → DTO 변환 생성자
    public DepartmentDTO(DepartmentEntity entity) {
        this.deptId = entity.getDeptId();
        this.deptName = entity.getDeptName();
    }
}