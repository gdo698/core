package com.core.erp.repository;

import com.core.erp.domain.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Integer> {
    Optional<EmployeeEntity> findByLoginId(String loginId);
    // findByLoginIdAndLoginPwd는 사용하지 않음 (보안상 권장하지 않음)
    boolean existsById(Integer empId);
}