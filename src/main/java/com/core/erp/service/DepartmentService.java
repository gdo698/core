package com.core.erp.service;

import com.core.erp.dto.employee.DepartmentDTO;
import com.core.erp.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    private final DepartmentRepository departmentRepository;

    public List<DepartmentDTO> getAllDepartments() {
        return departmentRepository.findAll().stream().map(entity -> {
            DepartmentDTO dto = new DepartmentDTO();
            dto.setDeptId(entity.getDeptId());
            dto.setDeptName(entity.getDeptName());
            // 필요시 추가 필드
            return dto;
        }).collect(Collectors.toList());
    }
} 