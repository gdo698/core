package com.core.erp.repository;

import com.core.erp.domain.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Integer> {

    EmployeeEntity findByLoginId(String loginId);

    EmployeeEntity findByLoginIdAndLoginPwd(String loginId, String loginPwd);
}
