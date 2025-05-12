package com.core.erp.repository;

import com.core.erp.domain.StoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<StoreEntity, Integer> {
    
    @Query("SELECT s FROM StoreEntity s WHERE s.storeStatus = :status")
    List<StoreEntity> findByStoreStatus(@Param("status") int status);
    
    @Query("SELECT s FROM StoreEntity s WHERE s.storeName LIKE %:keyword% OR s.storeAddr LIKE %:keyword%")
    List<StoreEntity> searchStores(@Param("keyword") String keyword);

    /**
     * 특정 상태의 점포 수 조회
     * @param storeStatus 점포 상태 (1: 영업중, 2: 휴업, 3: 폐업)
     * @return 점포 수
     */
    int countByStoreStatus(int storeStatus);

    /**
     * 특정 기간 내에 생성된 점포 수 조회
     * @param start 시작 일시
     * @param end 종료 일시
     * @return 생성된 점포 수
     */
    @Query("SELECT COUNT(s) FROM StoreEntity s WHERE s.storeCreatedAt BETWEEN :start AND :end")
    int countByStoreCreatedAtBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /**
     * 특정 일시 이전에 생성된 점포 수 조회
     * @param dateTime 기준 일시
     * @return 생성된 점포 수
     */
    @Query("SELECT COUNT(s) FROM StoreEntity s WHERE s.storeCreatedAt <= :dateTime")
    int countStoresByCreatedAtBefore(@Param("dateTime") LocalDateTime dateTime);
}
