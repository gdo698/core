package com.core.erp.repository;

import com.core.erp.domain.ApprLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApprLogRepository extends JpaRepository<ApprLogEntity, Integer> {
} 