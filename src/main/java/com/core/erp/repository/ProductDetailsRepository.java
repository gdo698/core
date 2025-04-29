package com.core.erp.repository;

import com.core.erp.domain.ProductDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductDetailsRepository extends JpaRepository<ProductDetailsEntity, Long> {
    ProductDetailsEntity findByProduct_ProductId(int productId);
}