package com.core.erp.controller;

import com.core.erp.dto.employee.EmployeeListDTO;
import com.core.erp.dto.employee.DepartmentDTO;
import com.core.erp.service.EmployeeListService;
import com.core.erp.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EmployeeListController {
    private final EmployeeListService employeeListService;
    private final DepartmentService departmentService;

    @GetMapping("/departments")
    public List<DepartmentDTO> getDepartments() {
        return departmentService.getAllDepartments();
    }

    @GetMapping("/employees")
    public List<EmployeeListDTO> getEmployeeLists(
        @RequestParam(required = false) String deptName,
        @RequestParam(required = false) String empName,
        @RequestParam(required = false) String empId,
        @RequestParam(defaultValue = "empId") String sort,
        @RequestParam(defaultValue = "asc") String order,
        @RequestParam(defaultValue = "HQ") String empType
    ) {
        return employeeListService.getEmployeeLists(deptName, empName, empId, sort, order, empType);
    }

    @GetMapping("/employees/{empId}")
    public EmployeeListDTO getEmployeeListDetail(@PathVariable Long empId) {
        return employeeListService.getEmployeeListDetail(empId);
    }
} 