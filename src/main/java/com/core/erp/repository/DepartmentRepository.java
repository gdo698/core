package com.core.erp.repository;

import com.core.erp.domain.DepartmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends JpaRepository<DepartmentEntity, Integer> {
    DepartmentEntity findByDeptName(String deptName);
} 