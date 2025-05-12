package com.core.erp.repository;

import com.core.erp.domain.SalesTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SalesTransactionRepository extends JpaRepository<SalesTransactionEntity, Integer> {

    // 매장 ID로 거래내역 조회 (결제일자 기준으로 최신순 정렬)
    List<SalesTransactionEntity> findByStore_StoreIdOrderByPaidAtDesc(Integer storeId);

    // 거래 ID로 조회 (거래 상세 정보)
    @Query("SELECT t FROM SalesTransactionEntity t WHERE t.transactionId = :transactionId")
    SalesTransactionEntity findByTransactionId(Integer transactionId);

    /**
     * 특정 기간 내 완료된 거래의 총 매출액 조회
     * @param start 시작 일시
     * @param end 종료 일시
     * @param transactionStatus 거래 상태 (0: 완료, 1: 환불, 2: 취소 등)
     * @return 총 매출액
     */
    @Query("SELECT SUM(t.finalAmount) FROM SalesTransactionEntity t WHERE t.paidAt BETWEEN :start AND :end AND t.transactionStatus = :status")
    Long sumFinalAmountByPaidAtBetweenAndTransactionStatus(
        @Param("start") LocalDateTime start, 
        @Param("end") LocalDateTime end,
        @Param("status") Integer transactionStatus
    );

    /**
     * 특정 점포의 특정 기간 내 완료된 거래의 총 매출액 조회
     * @param storeId 점포 ID
     * @param start 시작 일시
     * @param end 종료 일시
     * @param transactionStatus 거래 상태
     * @return 총 매출액
     */
    @Query("SELECT SUM(t.finalAmount) FROM SalesTransactionEntity t WHERE t.store.storeId = :storeId AND t.paidAt BETWEEN :start AND :end AND t.transactionStatus = :status")
    Long sumFinalAmountByStoreIdAndPaidAtBetweenAndTransactionStatus(
        @Param("storeId") Integer storeId,
        @Param("start") LocalDateTime start, 
        @Param("end") LocalDateTime end,
        @Param("status") Integer transactionStatus
    );
}
