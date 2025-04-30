package com.core.erp.repository;

import com.core.erp.domain.AnnualLeaveEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AnnualLeaveRepository extends JpaRepository<AnnualLeaveEntity, Integer> {
    Optional<AnnualLeaveEntity> findTopByEmployee_EmpIdOrderByYearDesc(int empId);
} 