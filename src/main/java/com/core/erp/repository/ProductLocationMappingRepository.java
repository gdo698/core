package com.core.erp.repository;

import com.core.erp.domain.ProductLocationMappingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductLocationMappingRepository extends JpaRepository<ProductLocationMappingEntity, Long> {

    // 상품 기준 매핑 조회 (상품 상세 진입 시 사용)
    Optional<ProductLocationMappingEntity> findByProduct_ProductId(Long productId);

    // 상품 매핑 삭제 (매핑 다시 하기 전 또는 취소할 때)
    void deleteByProduct_ProductIdAndStoreId(Long productId, Integer storeId);

    // 진열 위치 삭제 시 연결된 매핑도 삭제
    void deleteByStoreId(Integer storeId);

}
