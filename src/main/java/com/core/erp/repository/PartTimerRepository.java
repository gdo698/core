package com.core.erp.repository;

import com.core.erp.domain.PartTimerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface PartTimerRepository extends JpaRepository<PartTimerEntity, Integer> {

    /**
     * (1) 점주용 - storeId 조건 검색 (자기 지점만)
     */

    @Query("SELECT p FROM PartTimerEntity p " +
            "WHERE (:storeId IS NULL OR p.store.storeId = :storeId) " +
            "AND (:partName IS NULL OR p.partName LIKE %:partName%) " +
            "AND (:partStatus IS NULL OR p.partStatus = :partStatus) " +
            "AND (:partTimerId IS NULL OR p.partTimerId = :partTimerId)")
    Page<PartTimerEntity> searchStoreSide(
            @Param("storeId") Integer storeId,
            @Param("partName") String partName,
            @Param("partStatus") Integer partStatus,
            @Param("partTimerId") Integer partTimerId,
            Pageable pageable);

    /**
     * (2) 본사용 - storeId 조건 없이 전체 검색
     */
    @Query("SELECT p FROM PartTimerEntity p " +
            "WHERE (:partName IS NULL OR p.partName LIKE %:partName%) " +
            "AND (:partStatus IS NULL OR p.partStatus = :partStatus) " +
            "AND (:storeId IS NULL OR p.store.storeId = :storeId) " +
            "AND (:partTimerId IS NULL OR p.partTimerId = :partTimerId)")
    Page<PartTimerEntity> searchHeadquarterSide(
            @Param("partName") String partName,
            @Param("partStatus") Integer partStatus,
            @Param("storeId") Integer storeId,
            @Param("partTimerId") Integer partTimerId,
            Pageable pageable);

    Page<PartTimerEntity> findByStoreStoreId(Integer storeId,Pageable pageable);
}



