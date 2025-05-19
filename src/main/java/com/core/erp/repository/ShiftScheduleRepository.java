package com.core.erp.repository;

import com.core.erp.domain.ShiftScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ShiftScheduleRepository extends JpaRepository<ShiftScheduleEntity, Long> {


    Optional<ShiftScheduleEntity> findByPartTimer_PartTimerIdAndStartTimeBetween(
            Integer partTimerId,
            LocalDateTime start,
            LocalDateTime end
    );
}
