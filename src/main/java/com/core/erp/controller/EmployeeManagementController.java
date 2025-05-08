package com.core.erp.controller;

import com.core.erp.domain.DepartmentEntity;
import com.core.erp.domain.EmployeeEntity;
import com.core.erp.domain.StoreEntity;
import com.core.erp.dto.EmployeeManagementDTO;
import com.core.erp.repository.DepartmentRepository;
import com.core.erp.repository.EmployeeRepository;
import com.core.erp.repository.StoreRepository;
import com.core.erp.service.EmployeeManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class EmployeeManagementController {
    private final EmployeeManagementService employeeManagementService;
    private final StoreRepository storeRepository;
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    // 직원 관리 API
    @GetMapping("/api/employee-management/{empId}")
    public ResponseEntity<EmployeeManagementDTO> getEmployee(@PathVariable Integer empId) {
        EmployeeManagementDTO employee = employeeManagementService.getEmployeeById(empId);
        return ResponseEntity.ok(employee);
    }

    @PostMapping("/api/employee-management")
    public ResponseEntity<EmployeeManagementDTO> createEmployee(@RequestBody EmployeeManagementDTO employeeDTO) {
        EmployeeManagementDTO createdEmployee = employeeManagementService.createEmployee(employeeDTO);
        return ResponseEntity.ok(createdEmployee);
    }

    @PutMapping("/api/employee-management/{empId}")
    public ResponseEntity<EmployeeManagementDTO> updateEmployee(
            @PathVariable Integer empId,
            @RequestBody EmployeeManagementDTO employeeDTO) {
        employeeDTO.setEmpId(empId);
        EmployeeManagementDTO updatedEmployee = employeeManagementService.updateEmployee(employeeDTO);
        return ResponseEntity.ok(updatedEmployee);
    }
    
    /**
     * 점주 승인 및 매장 지정 API
     * 인사팀에서 점주 상태 변경 및 매장 할당에 사용
     */
    @PutMapping("/api/headquarters/hr/approve/store-owner/{empId}")
    public ResponseEntity<EmployeeManagementDTO> approveStoreOwner(
            @PathVariable Integer empId,
            @RequestParam String status,
            @RequestParam(required = false) Integer storeId,
            @RequestParam(required = false, defaultValue = "3") Integer departId) {
        System.out.println("===================================================");
        System.out.println("점주 승인 API 호출: empId=" + empId + ", status=" + status + ", storeId=" + storeId + ", departId=" + departId);
        System.out.println("===================================================");
        
        // 디버깅: 모든 부서 목록 출력
        System.out.println("\n=== 부서 목록 조회 시작 ===");
        List<DepartmentEntity> allDepartments = departmentRepository.findAll();
        System.out.println("전체 부서 수: " + allDepartments.size());
        for (DepartmentEntity dept : allDepartments) {
            System.out.println("부서: ID=" + dept.getDeptId() + ", 이름=" + dept.getDeptName());
        }
        System.out.println("=== 부서 목록 조회 종료 ===\n");
        
        // 직원 정보 조회
        EmployeeEntity employee = employeeRepository.findById(empId)
                .orElseThrow(() -> new RuntimeException("직원 정보를 찾을 수 없습니다. ID: " + empId));
        
        System.out.println("\n=== 업데이트 전 직원 정보 ===");
        System.out.println("empId: " + employee.getEmpId());
        System.out.println("empName: " + employee.getEmpName());
        System.out.println("empRole: " + employee.getEmpRole());
        System.out.println("empStatus: " + employee.getEmpStatus());
        System.out.println("workType: " + employee.getWorkType());
        System.out.println("department: " + (employee.getDepartment() != null ? 
                          employee.getDepartment().getDeptId() + "-" + employee.getDepartment().getDeptName() : "null"));
        System.out.println("store: " + (employee.getStore() != null ? 
                          employee.getStore().getStoreId() + "-" + employee.getStore().getStoreName() : "null"));
        System.out.println("=== 업데이트 전 직원 정보 종료 ===\n");
        
        // 직원 상태 업데이트
        employee.setEmpStatus(status);
        
        // 역할을 점주로 설정
        employee.setEmpRole("점주");
        
        // 근무 유형을 점주(3)로 설정
        employee.setWorkType(3);
        
        // 매장 지정이 있는 경우
        if (storeId != null) {
            StoreEntity store = storeRepository.findById(storeId)
                    .orElseThrow(() -> new RuntimeException("매장 정보를 찾을 수 없습니다. ID: " + storeId));
            
            // 직원과 매장 연결
            employee.setStore(store);
            System.out.println("점주에게 매장 할당: " + store.getStoreId() + " - " + store.getStoreName());
        }
        
        // 점주 부서 설정 (departId = 3, 점주 부서)
        try {
            // 강제로 점주 부서 ID 3으로 설정
            final int storeOwnerDeptId = 3; // 점주는 항상 부서 ID 3으로 고정
            
            // 부서 엔티티 조회 - ID가 3인 부서
            DepartmentEntity storeDepartment = departmentRepository.findById(storeOwnerDeptId)
                    .orElseThrow(() -> new RuntimeException("점주 부서(ID=" + storeOwnerDeptId + ")를 찾을 수 없습니다."));
            
            // 직접 값을 출력하여 확인
            System.out.println("\n=== 점주 부서 설정 시작 ===");
            System.out.println("storeDepartment.getDeptId(): " + storeDepartment.getDeptId());
            System.out.println("storeDepartment.getDeptName(): " + storeDepartment.getDeptName());
            
            // 직원과 부서 연결 - 명시적 할당
            employee.setDepartment(storeDepartment);
            
            // 디버깅을 위해 할당 후 바로 다시 출력
            System.out.println("할당 후 부서 ID: " + employee.getDepartment().getDeptId());
            System.out.println("할당 후 부서 이름: " + employee.getDepartment().getDeptName());
            System.out.println("=== 점주 부서 설정 종료 ===\n");
            
        } catch (Exception e) {
            System.err.println("점주 부서 설정 오류: " + e.getMessage());
            e.printStackTrace();
            // 부서 설정 실패시 롤백되지 않도록 예외 처리만 하고 진행
        }
        
        // 변경사항 저장
        EmployeeEntity updatedEmployee = employeeRepository.save(employee);
        
        System.out.println("\n=== 업데이트 후 직원 정보 ===");
        System.out.println("empId: " + updatedEmployee.getEmpId());
        System.out.println("empName: " + updatedEmployee.getEmpName());
        System.out.println("empRole: " + updatedEmployee.getEmpRole());
        System.out.println("empStatus: " + updatedEmployee.getEmpStatus());
        System.out.println("workType: " + updatedEmployee.getWorkType());
        System.out.println("department: " + (updatedEmployee.getDepartment() != null ? 
                          updatedEmployee.getDepartment().getDeptId() + "-" + updatedEmployee.getDepartment().getDeptName() : "null"));
        System.out.println("store: " + (updatedEmployee.getStore() != null ? 
                          updatedEmployee.getStore().getStoreId() + "-" + updatedEmployee.getStore().getStoreName() : "null"));
        System.out.println("=== 업데이트 후 직원 정보 종료 ===\n");
        
        // DTO로 변환하여 반환
        EmployeeManagementDTO resultDTO = new EmployeeManagementDTO();
        resultDTO.setEmpId(updatedEmployee.getEmpId());
        resultDTO.setEmpName(updatedEmployee.getEmpName());
        resultDTO.setEmpStatus(updatedEmployee.getEmpStatus());
        resultDTO.setEmpRole("STORE"); // 프론트엔드 호환성을 위해 STORE로 변환
        
        if (updatedEmployee.getStore() != null) {
            resultDTO.setStoreId(updatedEmployee.getStore().getStoreId());
            resultDTO.setStoreName(updatedEmployee.getStore().getStoreName());
        }
        
        // 부서 정보 포함
        if (updatedEmployee.getDepartment() != null) {
            resultDTO.setDeptCode(updatedEmployee.getDepartment().getDeptName());
            resultDTO.setDeptName(updatedEmployee.getDepartment().getDeptName());
        }
        
        System.out.println("점주 승인 완료: " + resultDTO);
        return ResponseEntity.ok(resultDTO);
    }
    
    // 점주 관리 API
    @GetMapping("/api/store-owners/{empId}")
    public ResponseEntity<EmployeeManagementDTO> getStoreOwner(@PathVariable Integer empId) {
        // 점주 조회 시에는 empRole을 STORE로 설정하여 검색
        System.out.println("점주 정보 조회 API 호출됨: empId = " + empId);
        
        try {
            EmployeeManagementDTO storeOwner = employeeManagementService.getEmployeeById(empId);
            
            // 점주 정보가 아닌 경우 STORE로 설정하여 강제 변경
            if (storeOwner != null) {
                // empRole이 점주가 아닌 경우 강제로 점주로 설정
                if (!"STORE".equals(storeOwner.getEmpRole()) && !"점주".equals(storeOwner.getEmpRole())) {
                    storeOwner.setEmpRole("STORE");
                }
                
                // 점주 관련 필드가 누락된 경우 기본값 설정
                if (storeOwner.getStoreName() == null || storeOwner.getStoreName().isEmpty()) {
                    storeOwner.setStoreName(storeOwner.getEmpName() + "의 매장");
                }
                
                if (storeOwner.getStoreAddr() == null || storeOwner.getStoreAddr().isEmpty()) {
                    storeOwner.setStoreAddr(storeOwner.getEmpAddr());
                }
                
                if (storeOwner.getStoreTel() == null || storeOwner.getStoreTel().isEmpty()) {
                    storeOwner.setStoreTel("02-" + (1000 + empId) + "-" + (2000 + empId));
                }
            }
            
            System.out.println("조회된 점주 정보: " + storeOwner);
            return ResponseEntity.ok(storeOwner);
        } catch (Exception e) {
            System.out.println("점주 정보 조회 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            
            // 오류 시 기본 점주 정보 반환
            EmployeeManagementDTO fallbackOwner = new EmployeeManagementDTO();
            fallbackOwner.setEmpId(empId);
            fallbackOwner.setEmpName("점주" + empId);
            fallbackOwner.setEmpRole("STORE");
            fallbackOwner.setEmpStatus("재직");
            fallbackOwner.setEmpPhone("010-1234-5678");
            fallbackOwner.setEmpEmail("store" + empId + "@example.com");
            fallbackOwner.setHireDate(LocalDate.now());
            fallbackOwner.setStoreName("매장" + empId);
            fallbackOwner.setStoreAddr("서울시 강남구");
            fallbackOwner.setStoreTel("02-" + (1000 + empId) + "-" + (2000 + empId));
            
            return ResponseEntity.ok(fallbackOwner);
        }
    }
    
    @PostMapping("/api/store-management")
    public ResponseEntity<EmployeeManagementDTO> createStoreOwner(@RequestBody EmployeeManagementDTO storeOwnerDTO) {
        // 점주 생성 시 empRole을 STORE로 설정
        storeOwnerDTO.setEmpRole("STORE");
        EmployeeManagementDTO createdStoreOwner = employeeManagementService.createEmployee(storeOwnerDTO);
        return ResponseEntity.ok(createdStoreOwner);
    }
    
    @PutMapping("/api/store-management/{empId}")
    public ResponseEntity<EmployeeManagementDTO> updateStoreOwner(
            @PathVariable Integer empId,
            @RequestBody EmployeeManagementDTO storeOwnerDTO) {
        storeOwnerDTO.setEmpId(empId);
        // 점주 업데이트 시 empRole을 STORE로 설정
        storeOwnerDTO.setEmpRole("STORE");
        EmployeeManagementDTO updatedStoreOwner = employeeManagementService.updateEmployee(storeOwnerDTO);
        return ResponseEntity.ok(updatedStoreOwner);
    }

    /**
     * 매장 목록 조회 (점주 지정 시 사용)
     * 점주가 없는 매장만 조회
     */
    @GetMapping("/api/stores/list")
    public ResponseEntity<List<Map<String, Object>>> getAllStores() {
        // 모든 매장 조회
        List<StoreEntity> allStores = storeRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();
        
        // 각 매장에 대해 점주 역할을 가진 직원이 있는지 확인
        for (StoreEntity store : allStores) {
            // 해당 매장에 점주 역할을 가진 직원이 있는지 확인
            List<EmployeeEntity> storeOwners = employeeRepository.findByStoreAndEmpRole(store, "점주");
            
            // 점주가 없는 매장만 결과에 추가
            if (storeOwners.isEmpty()) {
                Map<String, Object> storeMap = new HashMap<>();
                storeMap.put("storeId", store.getStoreId());
                storeMap.put("storeName", store.getStoreName());
                result.add(storeMap);
            }
        }
        
        System.out.println("점주 미지정 매장 목록 조회 결과: " + result);
        return ResponseEntity.ok(result);
    }
} 