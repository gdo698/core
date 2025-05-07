package com.core.erp.repository;

import com.core.erp.domain.LeaveReqEntity;
import com.core.erp.domain.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveReqRepository extends JpaRepository<LeaveReqEntity, Integer> {
    List<LeaveReqEntity> findByEmployee(EmployeeEntity employee);
} 