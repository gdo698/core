package com.core.erp.repository;

import com.core.erp.dto.TotalStockDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 본사용 지점 재고 조회 Repository 구현
 * 기존 StoreStockRepositoryImpl 코드를 분리하여 구현
 */
@Repository
@RequiredArgsConstructor
public class BranchStockRepositoryImpl {

    @PersistenceContext
    private final EntityManager em;

    /**
     * 본사에서 지점 재고 정보를 조회
     */
    public Page<TotalStockDTO> findBranchStockSummary(
            Integer storeId,
            String productName,
            Long barcode,
            Integer categoryId,
            Pageable pageable
    ) {
        // 메인 데이터 쿼리
        StringBuilder jpql = new StringBuilder();
        
        // storeId가 null인 경우와 아닌 경우를 분리하여 처리
        if (storeId == null) {
            // 모든 지점의 데이터를 조회하는 쿼리
            jpql.append("SELECT new com.core.erp.dto.TotalStockDTO(")
                    .append("p.productId, ")
                    .append("s.storeId, ")
                    .append("s.storeName, ")
                    .append("p.proName, ")
                    .append("p.proBarcode, ")
                    .append("c.categoryName, ")
                    .append("SUM(ss.quantity), ")
                    .append("(SELECT COALESCE(SUM(wse.quantity), 0L) FROM WarehouseStockEntity wse WHERE wse.product = p AND wse.store = s), ")
                    .append("SUM(ss.quantity) + (SELECT COALESCE(SUM(wse.quantity), 0L) FROM WarehouseStockEntity wse WHERE wse.product = p AND wse.store = s), ")
                    .append("MAX(shi.inDate), ")
                    .append("CASE p.isPromo WHEN 1 THEN '단종' WHEN 2 THEN '1+1' WHEN 3 THEN '2+1' ELSE '없음' END, ")
                    .append("NULL, ")
                    .append("NULL, ")
                    .append("NULL, ")
                    .append("FALSE")
                    .append(") ")
                    .append("FROM ProductEntity p ")
                    .append("LEFT JOIN p.category c ")
                    .append("LEFT JOIN c.parentCategory pc ")
                    .append("LEFT JOIN pc.parentCategory gpc ")
                    .append("LEFT JOIN StoreStockEntity ss ON ss.product = p ")
                    .append("LEFT JOIN ss.store s ")
                    .append("LEFT JOIN StockInHistoryEntity shi ON shi.product = p AND shi.store = s ")
                    .append("WHERE 1=1 ");
        } else {
            // 특정 지점의 데이터만 조회하는 쿼리
            jpql.append("SELECT new com.core.erp.dto.TotalStockDTO(")
                    .append("p.productId, ")
                    .append(":storeId, ")
                    .append("(SELECT s.storeName FROM StoreEntity s WHERE s.storeId = :storeId), ")
                    .append("p.proName, ")
                    .append("p.proBarcode, ")
                    .append("c.categoryName, ")
                    .append("(SELECT COALESCE(SUM(sse.quantity), 0L) FROM StoreStockEntity sse WHERE sse.product = p AND sse.store.storeId = :storeId), ")
                    .append("(SELECT COALESCE(SUM(wse.quantity), 0L) FROM WarehouseStockEntity wse WHERE wse.product = p AND wse.store.storeId = :storeId), ")
                    .append("(SELECT COALESCE(SUM(sse.quantity), 0L) FROM StoreStockEntity sse WHERE sse.product = p AND sse.store.storeId = :storeId) + ")
                    .append("(SELECT COALESCE(SUM(wse.quantity), 0L) FROM WarehouseStockEntity wse WHERE wse.product = p AND wse.store.storeId = :storeId), ")
                    .append("(SELECT MAX(shi.inDate) FROM StockInHistoryEntity shi WHERE shi.product = p AND shi.store.storeId = :storeId), ")
                    .append("CASE p.isPromo WHEN 1 THEN '단종' WHEN 2 THEN '1+1' WHEN 3 THEN '2+1' ELSE '없음' END, ")
                    .append("(SELECT CAST(sici.storeRealQuantity AS long) FROM StockInventoryCheckItemEntity sici JOIN sici.inventoryCheck sic WHERE sici.product = p AND sic.store.storeId = :storeId ORDER BY sic.checkDate DESC LIMIT 1), ")
                    .append("(SELECT CAST((sici.storeRealQuantity - sici.storePrevQuantity) AS long) FROM StockInventoryCheckItemEntity sici JOIN sici.inventoryCheck sic WHERE sici.product = p AND sic.store.storeId = :storeId ORDER BY sic.checkDate DESC LIMIT 1), ")
                    .append("(SELECT CAST(sic.checkId AS long) FROM StockInventoryCheckEntity sic WHERE sic.store.storeId = :storeId AND EXISTS (SELECT 1 FROM StockInventoryCheckItemEntity sici WHERE sici.inventoryCheck = sic AND sici.product = p) ORDER BY sic.checkDate DESC LIMIT 1), ")
                    .append("FALSE")
                    .append(") ")
                    .append("FROM ProductEntity p ")
                    .append("LEFT JOIN p.category c ")
                    .append("LEFT JOIN c.parentCategory pc ")
                    .append("LEFT JOIN pc.parentCategory gpc ")
                    .append("WHERE 1=1 ");
        }

        if (productName != null && !productName.isBlank())
            jpql.append("AND p.proName LIKE :productName ");
        if (barcode != null && barcode != 0)
            jpql.append("AND p.proBarcode = :barcode ");
        if (categoryId != null){
            jpql.append("AND (")
                    .append("c.categoryId   = :categoryId ")   // 소분류 직접 일치
                    .append("OR pc.categoryId = :categoryId ")  // 중분류 일치
                    .append("OR gpc.categoryId = :categoryId ")  // 대분류 일치
                    .append(") ");
        }

        if (storeId == null) {
            jpql.append("GROUP BY s.storeId, s.storeName, p.productId, p.proName, p.proBarcode, c.categoryName, p.isPromo");
        } else {
            jpql.append("GROUP BY p.productId, p.proName, p.proBarcode, c.categoryName, p.isPromo");
        }

        TypedQuery<TotalStockDTO> query = em.createQuery(jpql.toString(), TotalStockDTO.class);

        if (storeId != null) {
            query.setParameter("storeId", storeId);
        }
        
        if (productName != null && !productName.isBlank())
            query.setParameter("productName", "%" + productName + "%");
        if (barcode != null && barcode != 0)
            query.setParameter("barcode", barcode);
        if (categoryId != null)
            query.setParameter("categoryId", categoryId);

        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<TotalStockDTO> results = query.getResultList();

        // COUNT 쿼리 (페이징 정확하게)
        StringBuilder countJpql = new StringBuilder();
        
        if (storeId == null) {
            countJpql.append("SELECT COUNT(DISTINCT concat(p.productId, s.storeId)) ")
                    .append("FROM ProductEntity p ")
                    .append("LEFT JOIN p.category c ")
                    .append("LEFT JOIN c.parentCategory pc ")
                    .append("LEFT JOIN pc.parentCategory gpc ")
                    .append("LEFT JOIN StoreStockEntity ss ON ss.product = p ")
                    .append("LEFT JOIN ss.store s ")
                    .append("WHERE 1=1 ");
        } else {
            countJpql.append("SELECT COUNT(DISTINCT p.productId) ")
                    .append("FROM ProductEntity p ")
                    .append("LEFT JOIN p.category c ")
                    .append("LEFT JOIN c.parentCategory pc ")
                    .append("LEFT JOIN pc.parentCategory gpc ")
                    .append("WHERE 1=1 ");
        }

        if (productName != null && !productName.isBlank())
            countJpql.append("AND p.proName LIKE :productName ");
        if (barcode != null && barcode != 0)
            countJpql.append("AND p.proBarcode = :barcode ");
        if (categoryId != null) {
            countJpql.append("AND (")
                    .append("c.categoryId   = :categoryId ")
                    .append("OR pc.categoryId = :categoryId ")
                    .append("OR gpc.categoryId = :categoryId")
                    .append(") ");
        }

        TypedQuery<Long> countQuery = em.createQuery(countJpql.toString(), Long.class);

        if (productName != null && !productName.isBlank())
            countQuery.setParameter("productName", "%" + productName + "%");
        if (barcode != null && barcode != 0)
            countQuery.setParameter("barcode", barcode);
        if (categoryId != null)
            countQuery.setParameter("categoryId", categoryId);

        long totalRows = countQuery.getSingleResult();

        return new PageImpl<>(results, pageable, totalRows);
    }
} 