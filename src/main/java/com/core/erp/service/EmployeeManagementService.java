package com.core.erp.service;

import com.core.erp.domain.DepartmentEntity;
import com.core.erp.domain.EmployeeEntity;
import com.core.erp.dto.EmployeeListDTO;
import com.core.erp.dto.EmployeeManagementDTO;
import com.core.erp.repository.DepartmentRepository;
import com.core.erp.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class EmployeeManagementService {
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeListService employeeListService;

    public EmployeeManagementDTO getEmployeeById(Integer empId) {
        // 1. 디버깅 로그 추가
        System.out.println("EmployeeManagementService.getEmployeeById 호출됨: empId = " + empId);
        
        // 먼저 EmployeeListService에서 데이터를 가져와본다
        try {
            EmployeeListDTO listDTO = employeeListService.getEmployeeListDetail(empId.longValue());
            System.out.println("EmployeeListService에서 조회 결과: " + (listDTO != null ? "데이터 있음" : "데이터 없음"));
            
            if (listDTO != null) {
                // 2. 변환 전 EmployeeListDTO 데이터 로깅
                System.out.println("EmployeeListDTO 데이터: " + listDTO.toString());
                
                EmployeeManagementDTO result = convertFromEmployeeListDTO(listDTO);
                // 3. 변환 후 EmployeeManagementDTO 데이터 로깅
                System.out.println("변환된 EmployeeManagementDTO: " + result.toString());
                return result;
            }
        } catch (Exception e) {
            // 예외 로깅
            System.out.println("EmployeeListService 조회 중 예외 발생: " + e.getMessage());
            e.printStackTrace();
            // 예외 발생 시 기존 로직으로 진행
        }
        
        // 기존 로직
        EmployeeEntity employee = employeeRepository.findById(empId)
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + empId));
        
        System.out.println("직접 Repository에서 조회한 결과: " + employee.toString());
        EmployeeManagementDTO result = convertToDTO(employee);
        System.out.println("최종 변환 결과: " + result.toString());
        return result;
    }

    @Transactional
    public EmployeeManagementDTO createEmployee(EmployeeManagementDTO dto) {
        // ID 중복 체크
        if (dto.getEmpId() != null && employeeRepository.existsById(dto.getEmpId())) {
            throw new RuntimeException("Employee with ID " + dto.getEmpId() + " already exists");
        }

        EmployeeEntity entity = convertToEntity(dto);
        EmployeeEntity savedEmployee = employeeRepository.save(entity);
        return convertToDTO(savedEmployee);
    }

    @Transactional
    public EmployeeManagementDTO updateEmployee(EmployeeManagementDTO dto) {
        EmployeeEntity existingEmployee = employeeRepository.findById(dto.getEmpId())
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + dto.getEmpId()));

        // 기존 엔티티에 DTO 값 업데이트
        updateEmployeeFromDTO(existingEmployee, dto);
        
        EmployeeEntity updatedEmployee = employeeRepository.save(existingEmployee);
        return convertToDTO(updatedEmployee);
    }

    private EmployeeEntity convertToEntity(EmployeeManagementDTO dto) {
        EmployeeEntity entity = new EmployeeEntity();
        
        if (dto.getEmpId() != null) {
            entity.setEmpId(dto.getEmpId());
        }
        
        entity.setEmpName(dto.getEmpName());
        entity.setEmpStatus(dto.getEmpStatus());
        entity.setEmpPhone(dto.getEmpPhone());
        entity.setLoginId(dto.getEmpEmail()); // 이메일을 로그인 ID로 사용
        
        // 내선번호 설정
        if (dto.getEmpExt() != null && !dto.getEmpExt().isEmpty()) {
            try {
                entity.setEmpExt(Integer.parseInt(dto.getEmpExt()));
            } catch (NumberFormatException e) {
                // 숫자로 변환할 수 없는 경우 null로 설정
                entity.setEmpExt(null);
            }
        } else {
            entity.setEmpExt(null);
        }
        
        // 부서 설정
        if (dto.getDeptCode() != null && !dto.getDeptCode().isEmpty()) {
            DepartmentEntity department = departmentRepository.findByDeptName(dto.getDeptCode());
            entity.setDepartment(department);
        }
        
        // 입사일 설정
        if (dto.getHireDate() != null) {
            LocalDateTime hireDateTime = dto.getHireDate().atTime(LocalTime.MIDNIGHT);
            entity.setHireDate(hireDateTime);
        }
        
        // 필수 필드 기본값 설정 (신규 생성 시)
        if (dto.getEmpId() == null) {
            entity.setEmpRole("일반");
            entity.setEmpGender(0); // 기본값
            entity.setEmpAddr(""); // 기본값
            entity.setEmpBirth(""); // 기본값
            entity.setLoginPwd("defaultPassword"); // 기본 비밀번호
            entity.setEmpBank(0); // 기본값
            entity.setEmpAcount(""); // 기본값
            entity.setWorkType(1); // 기본값
        }
        
        return entity;
    }

    private void updateEmployeeFromDTO(EmployeeEntity entity, EmployeeManagementDTO dto) {
        if (dto.getEmpName() != null) {
            entity.setEmpName(dto.getEmpName());
        }
        
        if (dto.getEmpStatus() != null) {
            entity.setEmpStatus(dto.getEmpStatus());
        }
        
        if (dto.getEmpPhone() != null) {
            entity.setEmpPhone(dto.getEmpPhone());
        }
        
        if (dto.getEmpEmail() != null) {
            entity.setLoginId(dto.getEmpEmail());
        }
        
        // 내선번호 업데이트
        if (dto.getEmpExt() != null) {
            try {
                entity.setEmpExt(Integer.parseInt(dto.getEmpExt()));
            } catch (NumberFormatException e) {
                entity.setEmpExt(null);
            }
        }
        
        // 부서 업데이트
        if (dto.getDeptCode() != null && !dto.getDeptCode().isEmpty()) {
            DepartmentEntity department = departmentRepository.findByDeptName(dto.getDeptCode());
            entity.setDepartment(department);
        }
        
        // 입사일 업데이트
        if (dto.getHireDate() != null) {
            LocalDateTime hireDateTime = dto.getHireDate().atTime(LocalTime.MIDNIGHT);
            entity.setHireDate(hireDateTime);
        }
    }

    private EmployeeManagementDTO convertToDTO(EmployeeEntity entity) {
        EmployeeManagementDTO dto = new EmployeeManagementDTO();
        
        dto.setEmpId(entity.getEmpId());
        dto.setEmpName(entity.getEmpName());
        dto.setEmpStatus(entity.getEmpStatus());
        dto.setEmpPhone(entity.getEmpPhone());
        dto.setEmpEmail(entity.getLoginId());
        
        // 내선번호 설정
        if (entity.getEmpExt() != null) {
            dto.setEmpExt(entity.getEmpExt().toString());
        }
        
        // 부서 설정
        if (entity.getDepartment() != null) {
            dto.setDeptCode(entity.getDepartment().getDeptName());
            dto.setDeptName(entity.getDepartment().getDeptName());
        }
        
        // 입사일 설정
        if (entity.getHireDate() != null) {
            dto.setHireDate(entity.getHireDate().toLocalDate());
        }
        
        return dto;
    }
    
    // EmployeeListDTO를 EmployeeManagementDTO로 변환하는 메서드
    private EmployeeManagementDTO convertFromEmployeeListDTO(EmployeeListDTO listDTO) {
        EmployeeManagementDTO dto = new EmployeeManagementDTO();
        
        // 기본 정보 설정
        if (listDTO.getEmpId() != null) {
            dto.setEmpId(listDTO.getEmpId().intValue());
        }
        
        // 4. 수정: null 체크 추가하여 안전하게 데이터 복사
        dto.setEmpName(listDTO.getEmpName() != null ? listDTO.getEmpName() : "");
        dto.setEmpStatus(listDTO.getEmpStatus() != null ? listDTO.getEmpStatus() : "재직");
        dto.setEmpPhone(listDTO.getEmpPhone() != null ? listDTO.getEmpPhone() : "");
        dto.setEmpEmail(listDTO.getEmpEmail() != null ? listDTO.getEmpEmail() : "");
        dto.setEmpExt(listDTO.getEmpExt() != null ? listDTO.getEmpExt() : "");
        
        // 부서 정보 설정
        // 5. 수정: 부서 코드와 부서명 분리
        String deptName = listDTO.getDeptName();
        if (deptName != null && !deptName.isEmpty()) {
            dto.setDeptCode(deptName); // deptCode는 프론트엔드에서 선택 목록과 일치해야 함
            dto.setDeptName(deptName); // 표시용 부서명
        } else {
            dto.setDeptCode("");
            dto.setDeptName("");
        }
        
        // 입사일 설정 (문자열에서 LocalDate로 변환)
        if (listDTO.getHireDate() != null && !listDTO.getHireDate().isEmpty()) {
            try {
                // 6. 수정: 다양한 날짜 형식 지원
                String hireDateStr = listDTO.getHireDate();
                LocalDate hireDate;
                
                if (hireDateStr.contains("년") && hireDateStr.contains("월")) {
                    // "YYYY년 MM월 DD일" 형식 처리
                    hireDateStr = hireDateStr.replace("년 ", "-")
                                           .replace("월 ", "-")
                                           .replace("일", "");
                    hireDate = LocalDate.parse(hireDateStr, DateTimeFormatter.ofPattern("yyyy-M-d"));
                } else {
                    // 기본 ISO 형식 (yyyy-MM-dd) 가정
                    hireDate = LocalDate.parse(hireDateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                }
                
                dto.setHireDate(hireDate);
            } catch (Exception e) {
                System.out.println("입사일 변환 중 오류: " + e.getMessage());
                // 변환 실패 시 현재 날짜 사용
                dto.setHireDate(LocalDate.now());
            }
        }
        
        // 7. 수정: 디버깅 로그
        System.out.println("EmployeeListDTO -> EmployeeManagementDTO 변환 완료: " + dto);
        
        return dto;
    }
} 