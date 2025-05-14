package com.core.erp.repository;

import com.core.erp.domain.StockInventoryCheckEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockInventoryCheckRepository extends JpaRepository<StockInventoryCheckEntity, Long> {

}
