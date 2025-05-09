package com.core.erp.repository;

import com.core.erp.domain.StoreInquiryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StoreInquiryRepository extends JpaRepository<StoreInquiryEntity, Integer> {
    
    List<StoreInquiryEntity> findByStore_StoreId(Integer storeId);
    
    @Query("SELECT i FROM StoreInquiryEntity i WHERE i.inqType = :type")
    List<StoreInquiryEntity> findByInqType(@Param("type") int type);
    
    @Query("SELECT i FROM StoreInquiryEntity i WHERE i.inqStatus = :status")
    List<StoreInquiryEntity> findByInqStatus(@Param("status") int status);
    
    @Query("SELECT i FROM StoreInquiryEntity i WHERE i.store.storeId = :storeId AND i.inqType = :type")
    List<StoreInquiryEntity> findByStoreIdAndType(@Param("storeId") Integer storeId, @Param("type") int type);
    
    @Query("SELECT i FROM StoreInquiryEntity i WHERE i.store.storeId = :storeId AND i.inqStatus = :status")
    List<StoreInquiryEntity> findByStoreIdAndStatus(@Param("storeId") Integer storeId, @Param("status") int status);
} 