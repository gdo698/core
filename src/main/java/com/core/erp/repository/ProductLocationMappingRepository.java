package com.core.erp.repository;

import com.core.erp.domain.ProductLocationMappingEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductLocationMappingRepository extends JpaRepository<ProductLocationMappingEntity, Long> {


    // 상품 매핑 삭제 (매핑 다시 하기 전 또는 취소할 때)
    void deleteByProduct_ProductIdAndStoreId(Long productId, Integer storeId);

    // 진열 위치 삭제 시 연결된 매핑도 삭제
    @Modifying
    @Transactional
    @Query("DELETE FROM ProductLocationMappingEntity m WHERE m.location.locationId = :locationId")
    void deleteByLocationId(@Param("locationId") Long locationId);


    //  상품-매장 단위 전체 매핑 조회
    List<ProductLocationMappingEntity> findAllByProduct_ProductIdAndStoreId(Long productId, Integer storeId);

}
