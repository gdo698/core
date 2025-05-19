package com.core.erp.repository;

import com.core.erp.domain.SalesStatsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface SalesStatsRepository extends JpaRepository<SalesStatsEntity, Integer> {

    List<SalesStatsEntity> findByStore_StoreId(int storeId);

    List<SalesStatsEntity> findByStore_StoreIdAndSstDate(int storeId, LocalDate date);
}
