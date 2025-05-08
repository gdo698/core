package com.core.erp.repository;

import com.core.erp.domain.SalesTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SalesTransactionRepository extends JpaRepository<SalesTransactionEntity, Integer> {

    // 매장 ID로 거래내역 조회 (결제일자 기준으로 최신순 정렬)
    List<SalesTransactionEntity> findByStore_StoreIdOrderByPaidAtDesc(Integer storeId);

    // 거래 ID로 조회 (거래 상세 정보)
    @Query("SELECT t FROM SalesTransactionEntity t WHERE t.transactionId = :transactionId")
    SalesTransactionEntity findByTransactionId(Integer transactionId);
}
