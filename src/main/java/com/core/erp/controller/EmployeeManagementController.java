package com.core.erp.controller;

import com.core.erp.dto.EmployeeManagementDTO;
import com.core.erp.service.EmployeeManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employee-management")
@RequiredArgsConstructor
public class EmployeeManagementController {
    private final EmployeeManagementService employeeManagementService;

    @GetMapping("/{empId}")
    public ResponseEntity<EmployeeManagementDTO> getEmployee(@PathVariable Integer empId) {
        EmployeeManagementDTO employee = employeeManagementService.getEmployeeById(empId);
        return ResponseEntity.ok(employee);
    }

    @PostMapping
    public ResponseEntity<EmployeeManagementDTO> createEmployee(@RequestBody EmployeeManagementDTO employeeDTO) {
        EmployeeManagementDTO createdEmployee = employeeManagementService.createEmployee(employeeDTO);
        return ResponseEntity.ok(createdEmployee);
    }

    @PutMapping("/{empId}")
    public ResponseEntity<EmployeeManagementDTO> updateEmployee(
            @PathVariable Integer empId,
            @RequestBody EmployeeManagementDTO employeeDTO) {
        employeeDTO.setEmpId(empId);
        EmployeeManagementDTO updatedEmployee = employeeManagementService.updateEmployee(employeeDTO);
        return ResponseEntity.ok(updatedEmployee);
    }
} 