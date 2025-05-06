package com.core.erp.repository;

import com.core.erp.domain.AttendanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<AttendanceEntity, Integer> {
    List<AttendanceEntity> findByEmployee_EmpId(int empId);

    // 아르바이트 아이디 + 시간 범위
    List<AttendanceEntity> findByPartTimer_PartTimerIdAndInTimeBetween(
            Integer partTimerId, LocalDateTime start, LocalDateTime end
    );

    @Query(value = """
    SELECT * FROM attendance 
    WHERE part_timer_id = :partTimerId 
      AND store_id = :storeId 
      AND in_time BETWEEN :start AND :end
""", nativeQuery = true)
    List<AttendanceEntity> findWorkLog(
            @Param("partTimerId") Integer partTimerId,
            @Param("storeId") Integer storeId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

}

