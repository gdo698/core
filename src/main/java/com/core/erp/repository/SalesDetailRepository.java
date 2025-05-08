package com.core.erp.repository;

import com.core.erp.domain.SalesDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SalesDetailRepository extends JpaRepository<SalesDetailEntity, Integer> {

    @Query("SELECT d FROM SalesDetailEntity d " +
            "JOIN FETCH d.product p " +
            "LEFT JOIN FETCH p.category " +
            "WHERE d.transaction.transactionId = :transactionId")
    List<SalesDetailEntity> findWithProductByTransactionId(@Param("transactionId") Integer transactionId);
}

