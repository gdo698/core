package com.core.erp.repository;

import com.core.erp.domain.StoreEntity;  // 올바른 엔티티 경로로 수정
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<StoreEntity, Integer> {
    // Custom queries or basic findBy methods
}
