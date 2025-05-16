package com.core.erp.repository;

import com.core.erp.domain.EmployeeEntity;
import com.core.erp.domain.StoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<EmployeeEntity, Integer> {
    Optional<EmployeeEntity> findByLoginId(String loginId);
    // findByLoginIdAndLoginPwd는 사용하지 않음 (보안상 권장하지 않음)
    boolean existsById(Integer empId);
    
    @Query("SELECT e FROM EmployeeEntity e WHERE e.store = :store AND e.empRole = :role")
    List<EmployeeEntity> findByStoreAndEmpRole(@Param("store") StoreEntity store, @Param("role") String role);

    @Query("SELECT e FROM EmployeeEntity e WHERE e.department.deptId = :deptId")
    List<EmployeeEntity> findByDepartment_DeptId(@Param("deptId") int deptId);
}