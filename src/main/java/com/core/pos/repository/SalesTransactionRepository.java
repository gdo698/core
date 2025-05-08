package com.core.pos.repository;

import com.core.erp.domain.SalesTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface  SalesTransactionRepository extends JpaRepository<SalesTransactionEntity, Integer> {

    // 특정 점포의 거래내역을 결제일자 기준으로 최신순 정렬
    List<SalesTransactionEntity> findByStore_StoreIdOrderByPaidAtDesc(Integer storeId);
}
