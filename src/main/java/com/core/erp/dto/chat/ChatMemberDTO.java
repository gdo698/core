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
        ChatMemberDTO dto = ChatMemberDTO.builder()
                .empId(entity.getEmpId())
                .empName(entity.getEmpName())
                .empRole(entity.getEmpRole())
                .empImg(entity.getEmpImg())
                .build();
                
        if (entity.getDepartment() != null) {
            dto.setDeptId(entity.getDepartment().getDeptId());
            dto.setDeptName(entity.getDepartment().getDeptName());
        } else {
            dto.setDeptId(null);
            dto.setDeptName("미지정");
        }
        
        return dto;
    }
} 