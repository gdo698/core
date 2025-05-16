package com.core.erp.repository;

import com.core.erp.domain.DisplayLocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DisplayLocationRepository extends JpaRepository<DisplayLocationEntity, Long> {

    List<DisplayLocationEntity> findByStoreId(Integer storeId);

    @Modifying
    @Query("DELETE FROM DisplayLocationEntity d WHERE d.storeId = :storeId")
    void deleteByStoreId(@Param("storeId") Integer storeId);
}
