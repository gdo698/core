package com.core.erp.repository;

import com.core.erp.domain.SalaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SalaryRepository extends JpaRepository<SalaryEntity, Integer> {

    Optional<SalaryEntity> findTopByEmployee_EmpIdOrderByPayDateDesc(int empId);

    List<SalaryEntity> findByStore_StoreIdAndPayDateBetween(
            Integer storeId, LocalDateTime startDate, LocalDateTime endDate
    );

    List<SalaryEntity> findByPartTimer_PartTimerIdAndStore_StoreIdAndPayDateBetween(
            Integer partTimerId, Integer storeId, LocalDateTime startDate, LocalDateTime endDate
    );

    boolean existsByStore_StoreIdAndPayDateBetween(
            Integer storeId, LocalDateTime start, LocalDateTime end
    );
}
