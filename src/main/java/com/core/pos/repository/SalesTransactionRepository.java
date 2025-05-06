package com.core.pos.repository;

import com.core.erp.domain.SalesTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface  SalesTransactionRepository extends JpaRepository<SalesTransactionEntity, Integer> {
}
