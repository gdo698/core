package com.core.erp.service;

import com.core.erp.dto.EmployeeListDTO;
import com.core.erp.domain.EmployeeEntity;
import com.core.erp.domain.DepartmentEntity;
import com.core.erp.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeListService {
    private final EmployeeRepository employeeRepository;

    public List<EmployeeListDTO> getEmployeeLists(String deptName, String empName, String empId, String sort, String order) {
        List<EmployeeEntity> employees = employeeRepository.findAll(); // 실제 환경에서는 동적 쿼리로 교체

        return employees.stream()
                .filter(e -> deptName == null || deptName.isEmpty() || e.getDepartment().getDeptName().equals(deptName))
                .filter(e -> empName == null || empName.isEmpty() || e.getEmpName().contains(empName))
                .filter(e -> empId == null || empId.isEmpty() || Integer.toString(e.getEmpId()).equals(empId))
                .sorted((a, b) -> {
                    if ("empId".equals(sort)) {
                        return "desc".equals(order) ? Integer.compare(b.getEmpId(), a.getEmpId()) : Integer.compare(a.getEmpId(), b.getEmpId());
                    }
                    return 0;
                })
                .map(e -> {
                    EmployeeListDTO dto = new EmployeeListDTO();
                    dto.setEmpId((long) e.getEmpId());
                    dto.setEmpName(e.getEmpName());
                    dto.setDeptName(e.getDepartment().getDeptName());
                    dto.setEmpRole(e.getEmpRole());
                    dto.setEmpStatus(e.getEmpStatus());
                    dto.setHireDate(e.getHireDate().toString());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public EmployeeListDTO getEmployeeListDetail(Long empId) {
        EmployeeEntity e = employeeRepository.findById(empId.intValue()).orElseThrow();
        DepartmentEntity d = e.getDepartment();
        EmployeeListDTO dto = new EmployeeListDTO();
        dto.setEmpId((long) e.getEmpId());
        dto.setEmpName(e.getEmpName());
        dto.setEmpRole(e.getEmpRole());
        dto.setEmpStatus(e.getEmpStatus());
        dto.setHireDate(e.getHireDate().toString());
        dto.setDeptName(d.getDeptName());
        // 필요시 department의 다른 필드도 추가
        return dto;
    }
} 