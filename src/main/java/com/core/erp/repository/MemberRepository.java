package com.core.erp.repository;

import com.core.erp.domain.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<EmployeeEntity, Integer> {

    EmployeeEntity findByLoginId(String loginId);
}
