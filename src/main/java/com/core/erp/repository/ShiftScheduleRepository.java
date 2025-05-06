package com.core.erp.repository;

import com.core.erp.domain.ShiftScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShiftScheduleRepository extends JpaRepository<ShiftScheduleEntity, Long> {

    // 일정 시간 범위 내의 스케줄을 가져옴
    List<ShiftScheduleEntity> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
}
