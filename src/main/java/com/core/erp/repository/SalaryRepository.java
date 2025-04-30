package com.core.erp.repository;

import com.core.erp.domain.SalaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SalaryRepository extends JpaRepository<SalaryEntity, Integer> {
    Optional<SalaryEntity> findTopByEmployee_EmpIdOrderByPayDateDesc(int empId);
} 