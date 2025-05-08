package com.core.erp.repository;

import com.core.erp.domain.ProductEntity;
import com.core.erp.dto.OrderProductProjection;
import com.core.erp.dto.OrderProductResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    /**
     * 특정 매장의 제품 목록 + 재고 조회
     */
    @Query(value = """
    SELECT
        p.product_id AS productId,
        p.pro_name AS productName,
        p.pro_cost AS unitPrice,
        COALESCE(ss.quantity, 0) AS stockQty,
        p.pro_stock_limit AS proStockLimit,
        p.is_promo AS isPromo
    FROM product p
    LEFT JOIN store_stock ss 
        ON p.product_id = ss.product_id AND ss.store_id = :storeId
    WHERE p.is_promo IN (0, 2, 3)
      AND (:keyword IS NULL OR p.pro_name LIKE CONCAT('%', :keyword, '%'))
    ORDER BY p.pro_name
    LIMIT :limit OFFSET :offset
""", nativeQuery = true)
    List<OrderProductProjection> searchProductsWithStock(
            @Param("storeId") Integer storeId,
            @Param("keyword") String keyword,
            @Param("offset") int offset,
            @Param("limit") int limit
    );

    /**
     * 검색 조건에 맞는 제품 수 반환 (재고 포함 안 함)
     */
    @Query(value = """
        SELECT COUNT(*)
        FROM product p
        WHERE p.is_promo IN (0, 2, 3)
          AND (:keyword IS NULL OR p.pro_name LIKE CONCAT('%', :keyword, '%'))
    """, nativeQuery = true)
    int countProductsWithStock(
            @Param("storeId") Integer storeId,
            @Param("keyword") String keyword
    );

    /* 바코드로 상품 조회 (POS 바코드 기능용) */
    Optional<ProductEntity> findByProBarcode(Long proBarcode);
}
