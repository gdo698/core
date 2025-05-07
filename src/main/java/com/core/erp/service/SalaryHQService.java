package com.core.erp.service;

import com.core.erp.domain.SalaryEntity;
import com.core.erp.dto.SalaryDTO;
import com.core.erp.repository.SalaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SalaryHQService {

    private final SalaryRepository salaryRepository;

    /**
     * 본사 직원의 급여 내역 조회
     */
    public List<SalaryDTO> getMySalary(Integer empId) {
        List<SalaryEntity> salaryList = salaryRepository.findByEmployee_EmpIdOrderByPayDateDesc(empId);
        
        return salaryList.stream()
                .map(salary -> {
                    SalaryDTO dto = new SalaryDTO(salary);
                    return dto;
                })
                .collect(Collectors.toList());
    }
} 