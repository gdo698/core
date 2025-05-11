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

@Repository
@RequiredArgsConstructor
public class StoreStockRepositoryImpl implements StockRepositoryCustom {

    @PersistenceContext
    private final EntityManager em;

    @Override
    public Page<TotalStockDTO> findStockSummary(
            Integer storeId,
            String productName,
            Long barcode,
            Integer categoryId,
            Pageable pageable
    ) {
        // 메인 데이터 쿼리
        StringBuilder jpql = new StringBuilder();
        jpql.append("SELECT new com.core.erp.dto.TotalStockDTO(")
                .append(":storeId, ")
                .append("(SELECT s.storeName FROM StoreEntity s WHERE s.storeId = :storeId), ")
                .append("p.proName, p.proBarcode, c.categoryName, ")
                .append("(SELECT COALESCE(SUM(sse.quantity), 0) FROM StoreStockEntity sse WHERE sse.product = p AND sse.store.storeId = :storeId), ")
                .append("(SELECT COALESCE(SUM(wse.quantity), 0) FROM WarehouseStockEntity wse WHERE wse.product = p AND wse.store.storeId = :storeId), ")
                .append("(SELECT COALESCE(SUM(sse.quantity), 0) FROM StoreStockEntity sse WHERE sse.product = p AND sse.store.storeId = :storeId) + ")
                .append("(SELECT COALESCE(SUM(wse.quantity), 0) FROM WarehouseStockEntity wse WHERE wse.product = p AND wse.store.storeId = :storeId), ")
                .append("(SELECT MAX(shi.inDate) FROM StockInHistoryEntity shi WHERE shi.product = p AND shi.store.storeId = :storeId), ")
                .append("CASE p.isPromo WHEN 1 THEN '단종' WHEN 2 THEN '1+1' WHEN 3 THEN '2+1' ELSE '없음' END) ")
                .append("FROM ProductEntity p ")
                .append("LEFT JOIN p.category c ")
                .append("LEFT JOIN c.parentCategory pc ")
                .append("LEFT JOIN pc.parentCategory gpc ")
                .append("WHERE 1=1 ");

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

        jpql.append("GROUP BY p.productId, p.proName, p.proBarcode, c.categoryName, p.isPromo");

        TypedQuery<TotalStockDTO> query = em.createQuery(jpql.toString(), TotalStockDTO.class);

        if (storeId != null) query.setParameter("storeId", storeId);
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
        countJpql.append("SELECT COUNT(DISTINCT p.productId) ")
                .append("FROM ProductEntity p ")
                .append("LEFT JOIN p.category c ")
                .append("LEFT JOIN c.parentCategory pc ")
                .append("LEFT JOIN pc.parentCategory gpc ")
                .append("WHERE 1=1 ");

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
