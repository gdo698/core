package com.core.erp.repository;

import com.core.erp.domain.StoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoreRepository extends JpaRepository<StoreEntity, Integer> {
    
    @Query("SELECT s FROM StoreEntity s WHERE s.storeStatus = :status")
    List<StoreEntity> findByStoreStatus(@Param("status") int status);
    
    @Query("SELECT s FROM StoreEntity s WHERE s.storeName LIKE %:keyword% OR s.storeAddr LIKE %:keyword%")
    List<StoreEntity> searchStores(@Param("keyword") String keyword);
}
