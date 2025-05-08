package com.core.erp.repository;

import com.core.erp.domain.ProductDetailsEntity;
import com.core.erp.domain.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductDetailsRepository extends JpaRepository<ProductDetailsEntity, Long> {
    ProductDetailsEntity findByProduct_ProductId(int productId);

    /* 상품 Entity 기준으로 상세정보 조회 (POS 바코드용) */
    Optional<ProductDetailsEntity> findByProduct(ProductEntity product);
}
