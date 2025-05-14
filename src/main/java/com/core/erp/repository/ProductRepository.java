package com.core.erp.repository;

import com.core.erp.domain.ProductEntity;
import com.core.erp.dto.order.OrderProductProjection;
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
    p.pro_barcode AS barcode,
    c.category_name AS categoryName,
    COALESCE(s.quantity, 0) AS stockQty,
    p.pro_stock_limit AS proStockLimit,
    p.is_promo AS isPromo
FROM product p
LEFT JOIN category c ON p.category_id = c.category_id 
    LEFT JOIN store_stock s\s
    ON p.product_id = s.product_id\s
    AND (s.store_id = :storeId)
WHERE\s
    p.is_promo IN (0, 2, 3)
    AND (:productName IS NULL OR p.pro_name LIKE CONCAT('%', :productName, '%'))
    AND (:barcode IS NULL OR p.pro_barcode = :barcode)
    AND (:categoryId IS NULL OR p.category_id = :categoryId)
    AND (:isPromo IS NULL OR p.is_promo = :isPromo)
ORDER BY p.pro_name
LIMIT :limit OFFSET :offset
""", nativeQuery = true)
    List<OrderProductProjection> searchProductsWithStock(
            @Param("storeId") Integer storeId,
            @Param("productName") String productName,
            @Param("barcode") Long barcode,
            @Param("categoryId") Integer categoryId,
            @Param("isPromo") Integer isPromo,
            @Param("limit") int limit,
            @Param("offset") int offset
    );




    /**
     * 검색 조건에 맞는 제품 수 반환 (재고 포함 안 함)
     */
    @Query(value =
            """
    SELECT COUNT(*)
    FROM product p
LEFT JOIN store_stock s\s
    ON p.product_id = s.product_id AND s.store_id = :storeId
    WHERE p.is_promo IN (0, 2, 3)
      AND (s.store_id = :storeId OR s.store_id IS NULL)
      AND (:productName IS NULL OR p.pro_name LIKE CONCAT('%', :productName, '%'))
      AND (:barcode IS NULL OR p.pro_barcode = :barcode)
      AND (:categoryId IS NULL OR p.category_id = :categoryId)
      AND (:isPromo IS NULL OR p.is_promo = :isPromo)
""", nativeQuery = true)

    int countProductsWithStock(
            @Param("storeId") Integer storeId,
            @Param("productName") String productName,
            @Param("barcode") Long barcode,
            @Param("categoryId") Integer categoryId,
            @Param("isPromo") Integer isPromo
    );


    /* 바코드로 상품 조회 (POS 바코드 기능용) */
    Optional<ProductEntity> findByProBarcode(Long proBarcode);
}
