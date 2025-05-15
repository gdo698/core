package com.core.erp.dto.chat;

import com.core.erp.domain.EmployeeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMemberDTO {
    private Integer empId;
    private String empName;
    private String empRole;
    private String empImg;
    private Integer deptId;
    private String deptName;

    public static ChatMemberDTO fromEntity(EmployeeEntity entity) {
        return ChatMemberDTO.builder()
                .empId(entity.getEmpId())
                .empName(entity.getEmpName())
                .empRole(entity.getEmpRole())
                .empImg(entity.getEmpImg())
                .deptId(entity.getDepartment().getDeptId())
                .deptName(entity.getDepartment().getDeptName())
                .build();
    }
} 